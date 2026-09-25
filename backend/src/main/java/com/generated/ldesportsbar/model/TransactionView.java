package com.generated.ldesportsbar.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** 账户流水视图，金额正数为入账、负数为出账。 */
public record TransactionView(
  Long id,
  Long sessionId,
  String type,
  BigDecimal amount,
  Integer minutes,
  BigDecimal balanceAfter,
  String description,
  LocalDateTime createdAt
) {
}
