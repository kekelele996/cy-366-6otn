package com.generated.ldesportsbar.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {
  public static final String RECHARGE = "RECHARGE";
  public static final String PACKAGE_PURCHASE = "PACKAGE_PURCHASE";
  public static final String SESSION_PACKAGE_DEDUCT = "SESSION_PACKAGE_DEDUCT";
  public static final String SESSION_BALANCE_DEDUCT = "SESSION_BALANCE_DEDUCT";
  public static final String SESSION_PACKAGE_REFUND = "SESSION_PACKAGE_REFUND";
  public static final String SESSION_BALANCE_REFUND = "SESSION_BALANCE_REFUND";

  private Long id;
  private Long memberId;
  private Long sessionId;
  private Long packageId;
  private String type;
  private BigDecimal amount;
  private Integer minutes;
  private BigDecimal balanceAfter;
  private String description;
  private LocalDateTime createdAt;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getMemberId() {
    return memberId;
  }

  public void setMemberId(Long memberId) {
    this.memberId = memberId;
  }

  public Long getSessionId() {
    return sessionId;
  }

  public void setSessionId(Long sessionId) {
    this.sessionId = sessionId;
  }

  public Long getPackageId() {
    return packageId;
  }

  public void setPackageId(Long packageId) {
    this.packageId = packageId;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public Integer getMinutes() {
    return minutes;
  }

  public void setMinutes(Integer minutes) {
    this.minutes = minutes;
  }

  public BigDecimal getBalanceAfter() {
    return balanceAfter;
  }

  public void setBalanceAfter(BigDecimal balanceAfter) {
    this.balanceAfter = balanceAfter;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }
}
