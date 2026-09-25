package com.generated.ldesportsbar.service;

import com.generated.ldesportsbar.config.BillingConstants;
import com.generated.ldesportsbar.exception.ApiException;
import com.generated.ldesportsbar.mapper.AccountFlowMapper;
import com.generated.ldesportsbar.mapper.MemberMapper;
import com.generated.ldesportsbar.mapper.TimePackageMapper;
import com.generated.ldesportsbar.model.dto.MemberView;
import com.generated.ldesportsbar.model.entity.AccountFlow;
import com.generated.ldesportsbar.model.entity.Member;
import com.generated.ldesportsbar.model.entity.TimePackage;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberService {
  private final MemberMapper memberMapper;
  private final TimePackageMapper packageMapper;
  private final AccountFlowMapper flowMapper;

  public MemberService(MemberMapper memberMapper, TimePackageMapper packageMapper, AccountFlowMapper flowMapper) {
    this.memberMapper = memberMapper;
    this.packageMapper = packageMapper;
    this.flowMapper = flowMapper;
  }

  /** 按手机号建档；已存在则直接返回该会员（手机号即自然幂等键） */
  @Transactional
  public MemberView createOrFind(String phone, String name) {
    Member existing = memberMapper.findByPhone(phone);
    if (existing != null) {
      return buildView(existing);
    }
    Member member = new Member();
    member.setPhone(phone);
    member.setName(name == null ? "" : name.trim());
    member.setBalance(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
    member.setCreatedAt(LocalDateTime.now());
    try {
      memberMapper.insert(member);
    } catch (DuplicateKeyException duplicate) {
      return buildView(memberMapper.findByPhone(phone));
    }
    return buildView(member);
  }

  public List<Member> listMembers() {
    return memberMapper.findAll();
  }

  @Transactional
  public MemberView getView(Long memberId) {
    Member member = memberMapper.findById(memberId);
    if (member == null) {
      throw new ApiException("会员不存在");
    }
    packageMapper.expireStale(LocalDateTime.now());
    return buildView(member);
  }

  @Transactional
  public MemberView recharge(Long memberId, BigDecimal amount, String requestId) {
    Member member = memberMapper.findByIdForUpdate(memberId);
    if (member == null) {
      throw new ApiException("会员不存在");
    }
    if (isDuplicate(requestId)) {
      return buildView(member);
    }
    BigDecimal balanceAfter = member.getBalance().add(amount).setScale(2, RoundingMode.HALF_UP);
    member.setBalance(balanceAfter);
    memberMapper.updateBalance(member);

    AccountFlow flow = baseFlow(member, BillingConstants.FLOW_RECHARGE);
    flow.setRequestId(normalize(requestId));
    flow.setAmount(amount.setScale(2, RoundingMode.HALF_UP));
    flow.setRemark("余额充值");
    flowMapper.insert(flow);
    return buildView(member);
  }

  /** 购买 10 小时包：从余额扣款，余额不足则提示且账户不变 */
  @Transactional
  public MemberView purchasePackage(Long memberId, String requestId) {
    Member member = memberMapper.findByIdForUpdate(memberId);
    if (member == null) {
      throw new ApiException("会员不存在");
    }
    if (isDuplicate(requestId)) {
      return buildView(member);
    }
    if (member.getBalance().compareTo(BillingConstants.PACKAGE_PRICE) < 0) {
      throw new ApiException("余额不足，无法购买 " + BillingConstants.PACKAGE_HOURS + " 小时包（需 ¥"
          + BillingConstants.PACKAGE_PRICE + "），请先充值");
    }
    BigDecimal balanceAfter = member.getBalance().subtract(BillingConstants.PACKAGE_PRICE).setScale(2, RoundingMode.HALF_UP);
    member.setBalance(balanceAfter);
    memberMapper.updateBalance(member);

    LocalDateTime now = LocalDateTime.now();
    TimePackage timePackage = new TimePackage();
    timePackage.setMemberId(memberId);
    timePackage.setTotalMinutes(BillingConstants.PACKAGE_MINUTES);
    timePackage.setRemainingMinutes(BillingConstants.PACKAGE_MINUTES);
    timePackage.setPrice(BillingConstants.PACKAGE_PRICE);
    timePackage.setStatus(BillingConstants.PACKAGE_ACTIVE);
    timePackage.setPurchasedAt(now);
    timePackage.setExpiresAt(now.plusDays(BillingConstants.PACKAGE_VALID_DAYS));
    packageMapper.insert(timePackage);

    AccountFlow flow = baseFlow(member, BillingConstants.FLOW_PACKAGE_PURCHASE);
    flow.setRequestId(normalize(requestId));
    flow.setAmount(BillingConstants.PACKAGE_PRICE.negate());
    flow.setMinutes(BillingConstants.PACKAGE_MINUTES);
    flow.setRemark("购买 " + BillingConstants.PACKAGE_HOURS + " 小时包，"
        + BillingConstants.PACKAGE_VALID_DAYS + " 天有效");
    flowMapper.insert(flow);
    return buildView(member);
  }

  public List<AccountFlow> listFlows(Long memberId) {
    return flowMapper.findByMember(memberId);
  }

  private boolean isDuplicate(String requestId) {
    return normalize(requestId) != null && flowMapper.findByRequestId(normalize(requestId)) != null;
  }

  private String normalize(String requestId) {
    return requestId == null || requestId.isBlank() ? null : requestId.trim();
  }

  private AccountFlow baseFlow(Member member, String type) {
    AccountFlow flow = new AccountFlow();
    flow.setMemberId(member.getId());
    flow.setType(type);
    flow.setAmount(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
    flow.setMinutes(0);
    flow.setBalanceAfter(member.getBalance());
    flow.setRemark("");
    flow.setCreatedAt(LocalDateTime.now());
    return flow;
  }

  private MemberView buildView(Member member) {
    List<TimePackage> packages = packageMapper.findByMember(member.getId());
    LocalDateTime now = LocalDateTime.now();
    int totalRemaining = packages.stream()
        .filter(item -> BillingConstants.PACKAGE_ACTIVE.equals(item.getStatus()) && item.getExpiresAt().isAfter(now))
        .mapToInt(TimePackage::getRemainingMinutes)
        .sum();
    return new MemberView(member, packages, totalRemaining);
  }
}
