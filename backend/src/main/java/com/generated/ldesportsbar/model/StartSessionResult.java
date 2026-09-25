package com.generated.ldesportsbar.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** 开机结算结果。 */
public record StartSessionResult(
  Long sessionId,
  Long seatId,
  String seatNo,
  LocalDateTime startTime,
  Integer plannedMinutes,
  Integer packageMinutesUsed,
  BigDecimal balanceCharged,
  BigDecimal balanceAfter,
  boolean idempotentReplay
) {
}
