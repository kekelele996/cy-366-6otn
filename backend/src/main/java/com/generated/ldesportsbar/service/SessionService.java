package com.generated.ldesportsbar.service;

import com.generated.ldesportsbar.config.BillingConstants;
import com.generated.ldesportsbar.exception.ApiException;
import com.generated.ldesportsbar.mapper.AccountFlowMapper;
import com.generated.ldesportsbar.mapper.MachineMapper;
import com.generated.ldesportsbar.mapper.MachineSessionMapper;
import com.generated.ldesportsbar.mapper.MemberMapper;
import com.generated.ldesportsbar.mapper.SessionDeductionMapper;
import com.generated.ldesportsbar.mapper.TimePackageMapper;
import com.generated.ldesportsbar.model.dto.PowerOnRequest;
import com.generated.ldesportsbar.model.dto.SessionResult;
import com.generated.ldesportsbar.model.entity.AccountFlow;
import com.generated.ldesportsbar.model.entity.Machine;
import com.generated.ldesportsbar.model.entity.MachineSession;
import com.generated.ldesportsbar.model.entity.Member;
import com.generated.ldesportsbar.model.entity.SessionDeduction;
import com.generated.ldesportsbar.model.entity.TimePackage;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SessionService {
  private final MemberMapper memberMapper;
  private final MachineMapper machineMapper;
  private final MachineSessionMapper sessionMapper;
  private final SessionDeductionMapper deductionMapper;
  private final TimePackageMapper packageMapper;
  private final AccountFlowMapper flowMapper;

  public SessionService(MemberMapper memberMapper, MachineMapper machineMapper,
      MachineSessionMapper sessionMapper, SessionDeductionMapper deductionMapper,
      TimePackageMapper packageMapper, AccountFlowMapper flowMapper) {
    this.memberMapper = memberMapper;
    this.machineMapper = machineMapper;
    this.sessionMapper = sessionMapper;
    this.deductionMapper = deductionMapper;
    this.packageMapper = packageMapper;
    this.flowMapper = flowMapper;
  }

  /**
   * 开机结算：先扣最早到期的有效时长包，剩余时段按 6 元/小时扣余额。
   * 余额不足时整体回滚，机位保持空闲、账户不变。
   * requestId 为幂等键，同一开机请求重复提交不会重复扣费。
   */
  @Transactional
  public SessionResult powerOn(PowerOnRequest request) {
    MachineSession replay = sessionMapper.findByRequestId(request.requestId());
    if (replay != null) {
      return buildResult(replay, true);
    }

    Member member = memberMapper.findByIdForUpdate(request.memberId());
    if (member == null) {
      throw new ApiException("会员不存在");
    }
    // 持有会员行锁后复查，并发的相同请求在此串行化
    replay = sessionMapper.findByRequestId(request.requestId());
    if (replay != null) {
      return buildResult(replay, true);
    }

    Machine machine = machineMapper.findById(request.machineId());
    if (machine == null) {
      throw new ApiException("机位不存在");
    }
    if (machineMapper.occupy(machine.getId()) == 0) {
      throw new ApiException("机位 " + machine.getCode() + " 当前不可开机，请刷新后重试");
    }

    LocalDateTime now = LocalDateTime.now();
    packageMapper.expireStale(now);
    int plannedMinutes = request.plannedHours() * 60;
    List<TimePackage> validPackages = packageMapper.findValidByMember(member.getId(), now);

    List<TimePackage> touchedPackages = new ArrayList<>();
    List<Integer> packageUsages = new ArrayList<>();
    int remaining = plannedMinutes;
    for (TimePackage timePackage : validPackages) {
      if (remaining <= 0) {
        break;
      }
      int use = Math.min(timePackage.getRemainingMinutes(), remaining);
      if (use > 0) {
        touchedPackages.add(timePackage);
        packageUsages.add(use);
        remaining -= use;
      }
    }

    BigDecimal balanceCost = costOf(remaining);
    if (member.getBalance().compareTo(balanceCost) < 0) {
      BigDecimal lack = balanceCost.subtract(member.getBalance()).setScale(2, RoundingMode.HALF_UP);
      throw new ApiException("余额不足，还差 ¥" + lack + "，请先充值或购买时长包；机位未锁定，账户未扣款");
    }

    int packageMinutes = 0;
    for (int i = 0; i < touchedPackages.size(); i++) {
      TimePackage timePackage = touchedPackages.get(i);
      int use = packageUsages.get(i);
      timePackage.setRemainingMinutes(timePackage.getRemainingMinutes() - use);
      if (timePackage.getRemainingMinutes() == 0) {
        timePackage.setStatus(BillingConstants.PACKAGE_DEPLETED);
      }
      packageMapper.updateRemainingAndStatus(timePackage);
      packageMinutes += use;
    }

    member.setBalance(member.getBalance().subtract(balanceCost));
    memberMapper.updateBalance(member);

    MachineSession session = new MachineSession();
    session.setRequestId(request.requestId());
    session.setMemberId(member.getId());
    session.setMachineId(machine.getId());
    session.setPlannedMinutes(plannedMinutes);
    session.setPrepaidPackageMinutes(packageMinutes);
    session.setPrepaidAmount(balanceCost);
    session.setStatus(BillingConstants.SESSION_ACTIVE);
    session.setStartedAt(now);
    sessionMapper.insert(session);

    for (int i = 0; i < touchedPackages.size(); i++) {
      TimePackage timePackage = touchedPackages.get(i);
      int use = packageUsages.get(i);
      SessionDeduction deduction = new SessionDeduction();
      deduction.setSessionId(session.getId());
      deduction.setSourceType(BillingConstants.SOURCE_PACKAGE);
      deduction.setPackageId(timePackage.getId());
      deduction.setMinutes(use);
      deduction.setAmount(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
      deduction.setRefundedMinutes(0);
      deduction.setRefundedAmount(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
      deductionMapper.insert(deduction);
      insertFlow(member, session.getId(), BillingConstants.FLOW_PACKAGE_DEDUCT,
          BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), -use,
          "开机扣时长包 #" + timePackage.getId() + "（机位 " + machine.getCode() + "）");
    }
    if (balanceCost.signum() > 0) {
      SessionDeduction deduction = new SessionDeduction();
      deduction.setSessionId(session.getId());
      deduction.setSourceType(BillingConstants.SOURCE_BALANCE);
      deduction.setMinutes(remaining);
      deduction.setAmount(balanceCost);
      deduction.setRefundedMinutes(0);
      deduction.setRefundedAmount(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
      deductionMapper.insert(deduction);
      insertFlow(member, session.getId(), BillingConstants.FLOW_BALANCE_DEDUCT,
          balanceCost.negate(), 0,
          "开机预扣余额 " + remaining + " 分钟（机位 " + machine.getCode() + "）");
    }

    return buildResult(session, false);
  }

  /**
   * 停机结算：按实际使用分钟重算，多扣的时长退回原时长包、多扣的余额退回余额。
   * 重复停机直接返回首次结算结果，不会重复退款。
   */
  @Transactional
  public SessionResult powerOff(Long sessionId) {
    MachineSession session = sessionMapper.findByIdForUpdate(sessionId);
    if (session == null) {
      throw new ApiException("上机记录不存在");
    }
    if (BillingConstants.SESSION_CLOSED.equals(session.getStatus())) {
      return buildResult(session, true);
    }

    Member member = memberMapper.findByIdForUpdate(session.getMemberId());
    Machine machine = machineMapper.findById(session.getMachineId());
    String machineCode = machine == null ? "" : machine.getCode();
    LocalDateTime now = LocalDateTime.now();

    long seconds = Math.max(0, Duration.between(session.getStartedAt(), now).getSeconds());
    int actualMinutes = (int) Math.max(1, (seconds + 59) / 60);

    List<SessionDeduction> deductions = deductionMapper.findBySession(session.getId());
    int remaining = actualMinutes;
    int actualPackageMinutes = 0;
    BigDecimal actualBalanceCost = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

    for (SessionDeduction deduction : deductions) {
      if (BillingConstants.SOURCE_PACKAGE.equals(deduction.getSourceType())) {
        int used = Math.min(deduction.getMinutes(), remaining);
        remaining -= used;
        actualPackageMinutes += used;
        int refundMinutes = deduction.getMinutes() - used;
        if (refundMinutes > 0) {
          refundToPackage(deduction.getPackageId(), refundMinutes, now);
          deduction.setRefundedMinutes(refundMinutes);
          deductionMapper.updateRefund(deduction);
          insertFlow(member, session.getId(), BillingConstants.FLOW_PACKAGE_REFUND,
              BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), refundMinutes,
              "停机退时长包 #" + deduction.getPackageId() + "（机位 " + machineCode + "）");
        }
      } else {
        int usedMinutes = Math.min(deduction.getMinutes(), remaining);
        remaining -= usedMinutes;
        BigDecimal usedCost = costOf(usedMinutes);
        actualBalanceCost = actualBalanceCost.add(usedCost);
        BigDecimal refund = deduction.getAmount().subtract(usedCost);
        if (refund.signum() > 0) {
          member.setBalance(member.getBalance().add(refund));
          deduction.setRefundedAmount(refund);
          deductionMapper.updateRefund(deduction);
          insertFlow(member, session.getId(), BillingConstants.FLOW_BALANCE_REFUND,
              refund, 0, "停机退余额（机位 " + machineCode + "）");
        }
      }
    }

    // 超时使用：继续从有效时长包扣，再扣余额（余额可透支为负，下次充值后抵扣）
    if (remaining > 0) {
      packageMapper.expireStale(now);
      List<TimePackage> validPackages = packageMapper.findValidByMember(member.getId(), now);
      for (TimePackage timePackage : validPackages) {
        if (remaining <= 0) {
          break;
        }
        int use = Math.min(timePackage.getRemainingMinutes(), remaining);
        if (use <= 0) {
          continue;
        }
        timePackage.setRemainingMinutes(timePackage.getRemainingMinutes() - use);
        if (timePackage.getRemainingMinutes() == 0) {
          timePackage.setStatus(BillingConstants.PACKAGE_DEPLETED);
        }
        packageMapper.updateRemainingAndStatus(timePackage);
        remaining -= use;
        actualPackageMinutes += use;
        insertFlow(member, session.getId(), BillingConstants.FLOW_PACKAGE_DEDUCT,
            BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), -use,
            "超时补扣时长包 #" + timePackage.getId() + "（机位 " + machineCode + "）");
      }
      if (remaining > 0) {
        BigDecimal extraCost = costOf(remaining);
        member.setBalance(member.getBalance().subtract(extraCost));
        actualBalanceCost = actualBalanceCost.add(extraCost);
        insertFlow(member, session.getId(), BillingConstants.FLOW_BALANCE_DEDUCT,
            extraCost.negate(), 0, "超时补扣余额 " + remaining + " 分钟（机位 " + machineCode + "）");
      }
    }

    memberMapper.updateBalance(member);

    session.setStatus(BillingConstants.SESSION_CLOSED);
    session.setActualMinutes(actualMinutes);
    session.setActualPackageMinutes(actualPackageMinutes);
    session.setActualAmount(actualBalanceCost);
    session.setRefundAmount(deductions.stream()
        .map(item -> item.getRefundedAmount() == null ? BigDecimal.ZERO : item.getRefundedAmount())
        .reduce(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), BigDecimal::add));
    session.setEndedAt(now);
    sessionMapper.close(session);

    machineMapper.release(session.getMachineId());
    return buildResult(session, false);
  }

  public List<MachineSession> listActive() {
    return sessionMapper.findActive();
  }

  /** 多扣的时长按原路退回被扣的那个时长包 */
  private void refundToPackage(Long packageId, int minutes, LocalDateTime now) {
    TimePackage timePackage = packageMapper.findById(packageId);
    if (timePackage == null) {
      return;
    }
    timePackage.setRemainingMinutes(timePackage.getRemainingMinutes() + minutes);
    timePackage.setStatus(timePackage.getExpiresAt().isAfter(now)
        ? BillingConstants.PACKAGE_ACTIVE : BillingConstants.PACKAGE_EXPIRED);
    packageMapper.updateRemainingAndStatus(timePackage);
  }

  private BigDecimal costOf(int minutes) {
    return BillingConstants.MINUTE_RATE.multiply(BigDecimal.valueOf(minutes)).setScale(2, RoundingMode.HALF_UP);
  }

  private void insertFlow(Member member, Long sessionId, String type, BigDecimal amount, int minutes, String remark) {
    AccountFlow flow = new AccountFlow();
    flow.setMemberId(member.getId());
    flow.setSessionId(sessionId);
    flow.setType(type);
    flow.setAmount(amount.setScale(2, RoundingMode.HALF_UP));
    flow.setMinutes(minutes);
    flow.setBalanceAfter(member.getBalance());
    flow.setRemark(remark);
    flow.setCreatedAt(LocalDateTime.now());
    flowMapper.insert(flow);
  }

  private SessionResult buildResult(MachineSession session, boolean replayed) {
    Machine machine = machineMapper.findById(session.getMachineId());
    return new SessionResult(session, machine == null ? "" : machine.getCode(), replayed);
  }
}
