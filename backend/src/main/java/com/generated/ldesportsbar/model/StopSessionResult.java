package com.generated.ldesportsbar.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** 停机结算结果：按实际分钟重算后的净额与退回情况。 */
public record StopSessionResult(
  Long sessionId,
  Long seatId,
  String seatNo,
  LocalDateTime startTime,
  LocalDateTime endTime,
  Integer plannedMinutes,
  Integer actualMinutes,
  Integer packageMinutesUsed,
  Integer packageMinutesRefunded,
  BigDecimal balanceCharged,
  BigDecimal balanceRefunded,
  BigDecimal balanceAfter
) {
}
