package com.generated.ldesportsbar.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** 会员账户视图：余额、剩余时长（按有效时长包汇总）、时长包明细与流水。 */
public record MemberProfile(
  Long id,
  String phone,
  String name,
  BigDecimal balance,
  Integer remainingMinutes,
  Integer totalUsageMinutes,
  LocalDateTime createdAt,
  List<PackageView> packages,
  List<TransactionView> transactions
) {
}
