package com.generated.ldesportsbar.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** 会员名下的时长包视图。 */
public record PackageView(
  Long id,
  Integer packageMinutes,
  Integer remainingMinutes,
  LocalDateTime expiresAt,
  Boolean active
) {
}
