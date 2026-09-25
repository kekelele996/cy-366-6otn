package com.generated.ldesportsbar.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import com.generated.ldesportsbar.config.AppConstants;
import com.generated.ldesportsbar.exception.ConflictException;
import com.generated.ldesportsbar.exception.NotFoundException;
import com.generated.ldesportsbar.mapper.MemberMapper;
import com.generated.ldesportsbar.mapper.TimePackageMapper;
import com.generated.ldesportsbar.mapper.TransactionMapper;
import com.generated.ldesportsbar.model.Member;
import com.generated.ldesportsbar.model.MemberProfile;
import com.generated.ldesportsbar.model.PackageView;
import com.generated.ldesportsbar.model.TimePackage;
import com.generated.ldesportsbar.model.Transaction;
import com.generated.ldesportsbar.model.TransactionView;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberService {

  private final MemberMapper memberMapper;
  private final TimePackageMapper timePackageMapper;
  private final TransactionMapper transactionMapper;

  public MemberService(MemberMapper memberMapper,
                       TimePackageMapper timePackageMapper,
                       TransactionMapper transactionMapper) {
    this.memberMapper = memberMapper;
    this.timePackageMapper = timePackageMapper;
    this.transactionMapper = transactionMapper;
  }

  /** 按手机号建会员；已存在则报错，避免误操作。 */
  @Transactional
  public Member createMember(String phone, String name) {
    Member existing = memberMapper.findByPhone(phone);
    if (existing != null) {
      throw new ConflictException("该手机号已注册为会员");
    }
    Member member = new Member();
    member.setPhone(phone);
    member.setName(trimToNull(name));
    member.setBalance(BigDecimal.ZERO.setScale(2));
    member.setTotalUsageMinutes(0);
    memberMapper.insert(member);
    return member;
  }

  /** 余额充值。 */
  @Transactional
  public MemberProfile recharge(String phone, BigDecimal amount) {
    Member member = getMemberByPhoneForUpdate(phone);
    BigDecimal newBalance = member.getBalance().add(amount).setScale(2);
    memberMapper.updateBalance(member.getId(), newBalance);
    member.setBalance(newBalance);

    Transaction tx = new Transaction();
    tx.setMemberId(member.getId());
    tx.setType(Transaction.RECHARGE);
    tx.setAmount(amount.setScale(2));
    tx.setMinutes(0);
    tx.setBalanceAfter(newBalance);
    tx.setDescription(String.format("账户充值 %.2f 元", amount));
    transactionMapper.insert(tx);

    return getProfile(phone);
  }

  /** 购买 10 小时时长包：从余额扣款，新包 30 天有效。 */
  @Transactional
  public MemberProfile buyTenHourPackage(String phone) {
    Member member = getMemberByPhoneForUpdate(phone);
    if (member.getBalance().compareTo(AppConstants.PACKAGE_PRICE) < 0) {
      throw new ConflictException(String.format(
        "余额不足：10 小时包售价 %.2f 元，当前余额 %.2f 元，请先充值",
        AppConstants.PACKAGE_PRICE, member.getBalance()));
    }

    BigDecimal newBalance = member.getBalance().subtract(AppConstants.PACKAGE_PRICE).setScale(2);
    memberMapper.updateBalance(member.getId(), newBalance);
    member.setBalance(newBalance);

    LocalDateTime now = LocalDateTime.now();
    TimePackage pkg = new TimePackage();
    pkg.setMemberId(member.getId());
    pkg.setPackageMinutes(AppConstants.PACKAGE_MINUTES);
    pkg.setRemainingMinutes(AppConstants.PACKAGE_MINUTES);
    pkg.setExpiresAt(now.plusDays(AppConstants.PACKAGE_VALID_DAYS));
    timePackageMapper.insert(pkg);

    Transaction tx = new Transaction();
    tx.setMemberId(member.getId());
    tx.setPackageId(pkg.getId());
    tx.setType(Transaction.PACKAGE_PURCHASE);
    tx.setAmount(AppConstants.PACKAGE_PRICE.negate());
    tx.setMinutes(AppConstants.PACKAGE_MINUTES);
    tx.setBalanceAfter(newBalance);
    tx.setDescription(String.format("购买 10 小时时长包：%.2f 元，%d 天后到期",
      AppConstants.PACKAGE_PRICE, AppConstants.PACKAGE_VALID_DAYS));
    transactionMapper.insert(tx);

    return getProfile(phone);
  }

  /** 账户视图：余额、剩余时长、有效/历史时长包与流水。 */
  @Transactional(readOnly = true)
  public MemberProfile getProfile(String phone) {
    Member member = memberMapper.findByPhone(phone);
    if (member == null) {
      throw new NotFoundException("会员不存在，请先按手机号建档");
    }
    LocalDateTime now = LocalDateTime.now();
    int remainingMinutes = timePackageMapper.sumUsableMinutes(member.getId(), now);

    List<PackageView> packages = timePackageMapper.findByMemberId(member.getId()).stream()
      .map(p -> new PackageView(p.getId(), p.getPackageMinutes(), p.getRemainingMinutes(),
        p.getExpiresAt(), p.getRemainingMinutes() > 0 && p.getExpiresAt().isAfter(now)))
      .toList();

    List<TransactionView> transactions = transactionMapper.findByMemberId(member.getId()).stream()
      .map(t -> new TransactionView(t.getId(), t.getSessionId(), t.getType(), t.getAmount(),
        t.getMinutes(), t.getBalanceAfter(), t.getDescription(), t.getCreatedAt()))
      .toList();

    return new MemberProfile(member.getId(), member.getPhone(), member.getName(), member.getBalance(),
      remainingMinutes, member.getTotalUsageMinutes(), member.getCreatedAt(), packages, transactions);
  }

  private Member getMemberByPhoneForUpdate(String phone) {
    Member found = memberMapper.findByPhone(phone);
    if (found == null) {
      throw new NotFoundException("会员不存在，请先按手机号建档");
    }
    return memberMapper.lockById(found.getId());
  }

  private static String trimToNull(String value) {
    if (value == null) {
      return null;
    }
    String trimmed = value.trim();
    return trimmed.isEmpty() ? null : trimmed;
  }
}
