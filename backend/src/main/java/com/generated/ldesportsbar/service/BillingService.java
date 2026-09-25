package com.generated.ldesportsbar.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import com.generated.ldesportsbar.config.AppConstants;
import com.generated.ldesportsbar.exception.InsufficientFundsException;
import com.generated.ldesportsbar.mapper.MemberMapper;
import com.generated.ldesportsbar.mapper.TimePackageMapper;
import com.generated.ldesportsbar.mapper.TransactionMapper;
import com.generated.ldesportsbar.model.Member;
import com.generated.ldesportsbar.model.TimePackage;
import com.generated.ldesportsbar.model.Transaction;
import org.springframework.stereotype.Service;

/**
 * 计费核心：给定分钟数，先扣“最早到期”的有效时长包，剩余部分按 6 元/小时扣余额。
 * 调用方必须先锁定会员行（FOR UPDATE），本类的所有写操作都在调用方事务内执行。
 */
@Service
public class BillingService {

  private final MemberMapper memberMapper;
  private final TimePackageMapper timePackageMapper;
  private final TransactionMapper transactionMapper;

  public BillingService(MemberMapper memberMapper,
                        TimePackageMapper timePackageMapper,
                        TransactionMapper transactionMapper) {
    this.memberMapper = memberMapper;
    this.timePackageMapper = timePackageMapper;
    this.transactionMapper = transactionMapper;
  }

  /** 单次计费结果。 */
  public record ChargeResult(int packageMinutesUsed, BigDecimal balanceCharged, BigDecimal balanceAfter) {
  }

  /**
   * 对 minutesToCharge 执行计费；余额不足以支付时长包以外的部分时抛出异常，
   * 由外层事务整体回滚（时长包与余额都不变，机位保持空闲）。
   *
   * @param sessionId 关联开机单；可为 null（非开机场景预留）
   */
  public ChargeResult charge(Member member, int minutesToCharge, Long sessionId, LocalDateTime now) {
    int packageMinutesUsed = consumePackages(member, minutesToCharge, sessionId, now);
    int balanceMinutes = minutesToCharge - packageMinutesUsed;

    BigDecimal balanceCharged = BigDecimal.ZERO.setScale(2);
    if (balanceMinutes > 0) {
      BigDecimal cost = AppConstants.MINUTE_RATE.multiply(BigDecimal.valueOf(balanceMinutes)).setScale(2);
      BigDecimal balance = member.getBalance();
      if (balance.compareTo(cost) < 0) {
        throw new InsufficientFundsException(cost, balance);
      }
      balance = balance.subtract(cost).setScale(2);
      memberMapper.updateBalance(member.getId(), balance);
      member.setBalance(balance);
      balanceCharged = cost;

      recordTransaction(member, sessionId, null, Transaction.SESSION_BALANCE_DEDUCT,
        cost.negate(), balanceMinutes, balance,
        String.format("开机余额扣费：%d 分钟，扣 %.2f 元（6 元/小时）", balanceMinutes, cost));
    }
    return new ChargeResult(packageMinutesUsed, balanceCharged, member.getBalance());
  }

  /**
   * 按最早到期优先扣减时长包，返回实际从时长包扣除的分钟数。
   */
  private int consumePackages(Member member, int minutes, Long sessionId, LocalDateTime now) {
    List<TimePackage> packages = timePackageMapper.lockUsable(member.getId(), now);
    packages.sort(Comparator.comparing(TimePackage::getExpiresAt).thenComparing(TimePackage::getId));

    int remaining = minutes;
    int used = 0;
    for (TimePackage pkg : packages) {
      if (remaining == 0) {
        break;
      }
      int take = Math.min(remaining, pkg.getRemainingMinutes());
      if (take <= 0) {
        continue;
      }
      int newRemaining = pkg.getRemainingMinutes() - take;
      timePackageMapper.updateRemaining(pkg.getId(), newRemaining);
      recordTransaction(member, sessionId, pkg.getId(), Transaction.SESSION_PACKAGE_DEDUCT,
        BigDecimal.ZERO.setScale(2), take, member.getBalance(),
        String.format("开机时长包扣费：扣 %d 分钟（包到期 %s）", take, pkg.getExpiresAt()));
      remaining -= take;
      used += take;
    }
    return used;
  }

  /** 记一笔流水。amount 正数入账、负数出账；时长包类流水金额为 0，分钟数体现在 minutes。 */
  private void recordTransaction(Member member, Long sessionId, Long packageId, String type,
                                 BigDecimal amount, int minutes, BigDecimal balanceAfter, String description) {
    Transaction tx = new Transaction();
    tx.setMemberId(member.getId());
    tx.setSessionId(sessionId);
    tx.setPackageId(packageId);
    tx.setType(type);
    tx.setAmount(amount.setScale(2));
    tx.setMinutes(minutes);
    tx.setBalanceAfter(balanceAfter.setScale(2));
    tx.setDescription(description);
    transactionMapper.insert(tx);
  }
}
