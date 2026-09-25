package com.generated.ldesportsbar.model.entity;

import java.math.BigDecimal;

public class SessionDeduction {
  private Long id;
  private Long sessionId;
  private String sourceType;
  private Long packageId;
  private Integer minutes;
  private BigDecimal amount;
  private Integer refundedMinutes;
  private BigDecimal refundedAmount;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getSessionId() {
    return sessionId;
  }

  public void setSessionId(Long sessionId) {
    this.sessionId = sessionId;
  }

  public String getSourceType() {
    return sourceType;
  }

  public void setSourceType(String sourceType) {
    this.sourceType = sourceType;
  }

  public Long getPackageId() {
    return packageId;
  }

  public void setPackageId(Long packageId) {
    this.packageId = packageId;
  }

  public Integer getMinutes() {
    return minutes;
  }

  public void setMinutes(Integer minutes) {
    this.minutes = minutes;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public Integer getRefundedMinutes() {
    return refundedMinutes;
  }

  public void setRefundedMinutes(Integer refundedMinutes) {
    this.refundedMinutes = refundedMinutes;
  }

  public BigDecimal getRefundedAmount() {
    return refundedAmount;
  }

  public void setRefundedAmount(BigDecimal refundedAmount) {
    this.refundedAmount = refundedAmount;
  }
}
