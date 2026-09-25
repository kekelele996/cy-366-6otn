package com.generated.ldesportsbar.model.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TimePackage {
  private Long id;
  private Long memberId;
  private Integer totalMinutes;
  private Integer remainingMinutes;
  private BigDecimal price;
  private String status;
  private LocalDateTime purchasedAt;
  private LocalDateTime expiresAt;

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

  public Integer getTotalMinutes() {
    return totalMinutes;
  }

  public void setTotalMinutes(Integer totalMinutes) {
    this.totalMinutes = totalMinutes;
  }

  public Integer getRemainingMinutes() {
    return remainingMinutes;
  }

  public void setRemainingMinutes(Integer remainingMinutes) {
    this.remainingMinutes = remainingMinutes;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public LocalDateTime getPurchasedAt() {
    return purchasedAt;
  }

  public void setPurchasedAt(LocalDateTime purchasedAt) {
    this.purchasedAt = purchasedAt;
  }

  public LocalDateTime getExpiresAt() {
    return expiresAt;
  }

  public void setExpiresAt(LocalDateTime expiresAt) {
    this.expiresAt = expiresAt;
  }
}
