package com.generated.ldesportsbar.service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import com.generated.ldesportsbar.config.AppConstants;
import com.generated.ldesportsbar.exception.ConflictException;
import com.generated.ldesportsbar.exception.NotFoundException;
import com.generated.ldesportsbar.mapper.MemberMapper;
import com.generated.ldesportsbar.mapper.SeatMapper;
import com.generated.ldesportsbar.mapper.SessionMapper;
import com.generated.ldesportsbar.mapper.TimePackageMapper;
import com.generated.ldesportsbar.mapper.TransactionMapper;
import com.generated.ldesportsbar.model.Member;
import com.generated.ldesportsbar.model.Seat;
import com.generated.ldesportsbar.model.Session;
import com.generated.ldesportsbar.model.StartSessionResult;
import com.generated.ldesportsbar.model.StopSessionResult;
import com.generated.ldesportsbar.model.TimePackage;
import com.generated.ldesportsbar.model.Transaction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SessionService {

  private final MemberMapper memberMapper;
  private final SeatMapper seatMapper;
  private final SessionMapper sessionMapper;
  private final TimePackageMapper timePackageMapper;
  private final TransactionMapper transactionMapper;
  private final BillingService billingService;

  public SessionService(MemberMapper memberMapper,
                        SeatMapper seatMapper,
                        SessionMapper sessionMapper,
                        TimePackageMapper timePackageMapper,
                        TransactionMapper transactionMapper,
                        BillingService billingService) {
    this.memberMapper = memberMapper;
    this.seatMapper = seatMapper;
    this.sessionMapper = sessionMapper;
    this.timePackageMapper = timePackageMapper;
    this.transactionMapper = transactionMapper;
    this.billingService = billingService;
  }

  /**
   * 开机结算。
   * <ol>
   *   <li>幂等键命中已有开机单 → 原样返回，不重复扣费；</li>
   *   <li>校验机位空闲、会员没有其他进行中的开机单；</li>
   *   <li>按预计时长先扣最早到期的有效时长包，剩余分钟按 6 元/小时扣余额；</li>
   *   <li>余额不足抛异常，事务回滚，机位保持空闲、账户不变。</li>
   * </ol>
   */
  @Transactional
  public StartSessionResult start(String phone, Long seatId, int plannedMinutes, String idempotencyKey) {
    Member member = memberMapper.findByPhone(phone);
    if (member == null) {
      throw new NotFoundException("会员不存在，请先按手机号建档");
    }

    // 幂等：同一开机请求重复提交，直接返回原开机单
    Session existing = sessionMapper.findByIdempotencyKey(idempotencyKey);
    if (existing != null) {
      Seat seat = seatMapper.findById(existing.getSeatId());
      return buildStartResult(existing, seat, true);
    }

    // 锁定会员行，与充值/买包/其他开机串行
    member = memberMapper.lockById(member.getId());

    if (sessionMapper.countActiveByMember(member.getId()) > 0) {
      throw new ConflictException("该会员已有进行中的开机单，请先停机再开新机位");
    }

    Seat seat = seatMapper.lockById(seatId);
    if (seat == null) {
      throw new NotFoundException("机位不存在");
    }
    if (AppConstants.SEAT_BROKEN.equals(seat.getStatus())) {
      throw new ConflictException("该机位故障中，暂不能开机");
    }
    if (!AppConstants.SEAT_IDLE.equals(seat.getStatus())) {
      throw new ConflictException("该机位不是空闲状态，无法开机");
    }

    LocalDateTime now = LocalDateTime.now();

    // 先建立开机单，扣费流水即可直接关联 session_id；后续若余额不足整体回滚，开机单也不会残留
    Session session = new Session();
    session.setMemberId(member.getId());
    session.setSeatId(seat.getId());
    session.setIdempotencyKey(idempotencyKey);
    session.setPlannedMinutes(plannedMinutes);
    session.setStatus(AppConstants.SESSION_ACTIVE);
    sessionMapper.insert(session);

    // 按预计时长预扣：先最早到期的有效时长包，剩余分钟按 6 元/小时扣余额；
    // 余额不足抛异常 → 事务回滚，开机单撤销、机位保持空闲、账户不变
    BillingService.ChargeResult charge =
      billingService.charge(member, plannedMinutes, session.getId(), now);

    // 扣费成功后占用机位（行锁保证不会并发重复占用）
    seatMapper.compareAndSetStatus(seat.getId(), AppConstants.SEAT_IDLE, AppConstants.SEAT_IN_USE);

    return buildStartResult(session, seat, false, charge);
  }

  /**
   * 停机结算：按实际使用分钟（向上取整到分钟）重算。
   * 先把开机时的预扣（时长包/余额）按原路全额退回，再对实际分钟重新计费，
   * 多扣的时长或余额自然回到对应账户，不会退到错误的资金来源。
   */
  @Transactional
  public StopSessionResult stop(Long sessionId) {
    Session session = sessionMapper.lockById(sessionId);
    if (session == null) {
      throw new NotFoundException("开机单不存在");
    }
    if (!AppConstants.SESSION_ACTIVE.equals(session.getStatus())) {
      throw new ConflictException("该开机单已停机，请勿重复结算");
    }

    LocalDateTime now = LocalDateTime.now();
    int actualMinutes = (int) Duration.between(session.getStartTime(), now).toMinutes();
    if (actualMinutes < 0) {
      actualMinutes = 0;
    }

    Member member = memberMapper.lockById(session.getMemberId());

    // 1) 原路退回开机时的全部预扣
    List<Transaction> deductions = transactionMapper.findStartDeductions(session.getId());
    BigDecimal balance = member.getBalance();
    for (Transaction tx : deductions) {
      if (Transaction.SESSION_BALANCE_DEDUCT.equals(tx.getType())) {
        balance = balance.add(tx.getAmount().abs()).setScale(2);
        memberMapper.updateBalance(member.getId(), balance);
        member.setBalance(balance);
        Transaction refund = baseTransaction(member, session.getId(), null,
          Transaction.SESSION_BALANCE_REFUND, tx.getAmount().abs(), 0, balance);
        refund.setDescription(String.format("停机重算退回余额：%.2f 元", tx.getAmount().abs()));
        transactionMapper.insert(refund);
      } else if (Transaction.SESSION_PACKAGE_DEDUCT.equals(tx.getType())) {
        TimePackage pkg = tx.getPackageId() == null ? null : timePackageMapper.lockById(tx.getPackageId());
        if (pkg != null) {
          int restored = pkg.getRemainingMinutes() + tx.getMinutes();
          timePackageMapper.updateRemaining(pkg.getId(), restored);
        }
        Transaction refund = baseTransaction(member, session.getId(), tx.getPackageId(),
          Transaction.SESSION_PACKAGE_REFUND, BigDecimal.ZERO.setScale(2), tx.getMinutes(), balance);
        refund.setDescription(String.format("停机重算退回时长包：%d 分钟", tx.getMinutes()));
        transactionMapper.insert(refund);
      }
    }

    // 2) 对实际分钟重新计费（先最早到期时长包，再余额）
    BillingService.ChargeResult actualCharge = billingService.charge(member, actualMinutes, session.getId(), now);

    // 3) 结账：累计实际时长、释放机位
    int newTotal = member.getTotalUsageMinutes() + actualMinutes;
    memberMapper.updateTotalUsage(member.getId(), newTotal);

    sessionMapper.finish(session.getId(), AppConstants.SESSION_STOPPED, now, actualMinutes);
    seatMapper.updateStatus(session.getSeatId(), AppConstants.SEAT_IDLE);

    Seat seat = seatMapper.findById(session.getSeatId());

    int plannedPackageMinutes = deductions.stream()
      .filter(t -> Transaction.SESSION_PACKAGE_DEDUCT.equals(t.getType()))
      .mapToInt(Transaction::getMinutes).sum();
    BigDecimal plannedBalanceCharge = deductions.stream()
      .filter(t -> Transaction.SESSION_BALANCE_DEDUCT.equals(t.getType()))
      .map(Transaction::getAmount).map(BigDecimal::abs).reduce(BigDecimal.ZERO, BigDecimal::add);

    int packageRefunded = Math.max(0, plannedPackageMinutes - actualCharge.packageMinutesUsed());
    BigDecimal balanceRefunded = plannedBalanceCharge.subtract(actualCharge.balanceCharged()).max(BigDecimal.ZERO);

    return new StopSessionResult(session.getId(), seat.getId(), seat.getSeatNo(),
      session.getStartTime(), now, session.getPlannedMinutes(), actualMinutes,
      actualCharge.packageMinutesUsed(), packageRefunded,
      actualCharge.balanceCharged(), balanceRefunded.setScale(2), actualCharge.balanceAfter());
  }

  private Transaction baseTransaction(Member member, Long sessionId, Long packageId, String type,
                                      BigDecimal amount, int minutes, BigDecimal balanceAfter) {
    Transaction tx = new Transaction();
    tx.setMemberId(member.getId());
    tx.setSessionId(sessionId);
    tx.setPackageId(packageId);
    tx.setType(type);
    tx.setAmount(amount.setScale(2));
    tx.setMinutes(minutes);
    tx.setBalanceAfter(balanceAfter.setScale(2));
    return tx;
  }

  private StartSessionResult buildStartResult(Session session, Seat seat, boolean replay) {
    List<Transaction> deductions = transactionMapper.findStartDeductions(session.getId());
    int packageMinutes = deductions.stream()
      .filter(t -> Transaction.SESSION_PACKAGE_DEDUCT.equals(t.getType()))
      .mapToInt(Transaction::getMinutes).sum();
    BigDecimal balanceCharged = deductions.stream()
      .filter(t -> Transaction.SESSION_BALANCE_DEDUCT.equals(t.getType()))
      .map(Transaction::getAmount).map(BigDecimal::abs).reduce(BigDecimal.ZERO, BigDecimal::add);
    Member member = memberMapper.findById(session.getMemberId());
    return new StartSessionResult(session.getId(), seat.getId(), seat.getSeatNo(),
      session.getStartTime(), session.getPlannedMinutes(), packageMinutes,
      balanceCharged.setScale(2), member.getBalance(), replay);
  }

  private StartSessionResult buildStartResult(Session session, Seat seat, boolean replay,
                                              BillingService.ChargeResult charge) {
    return new StartSessionResult(session.getId(), seat.getId(), seat.getSeatNo(),
      session.getStartTime(), session.getPlannedMinutes(), charge.packageMinutesUsed(),
      charge.balanceCharged(), charge.balanceAfter(), replay);
  }
}
