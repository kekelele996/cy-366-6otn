package com.generated.ldesportsbar.model;

import java.time.LocalDateTime;

public class TimePackage {
  private Long id;
  private Long memberId;
  private Integer packageMinutes;
  private Integer remainingMinutes;
  private LocalDateTime expiresAt;
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

  public Integer getPackageMinutes() {
    return packageMinutes;
  }

  public void setPackageMinutes(Integer packageMinutes) {
    this.packageMinutes = packageMinutes;
  }

  public Integer getRemainingMinutes() {
    return remainingMinutes;
  }

  public void setRemainingMinutes(Integer remainingMinutes) {
    this.remainingMinutes = remainingMinutes;
  }

  public LocalDateTime getExpiresAt() {
    return expiresAt;
  }

  public void setExpiresAt(LocalDateTime expiresAt) {
    this.expiresAt = expiresAt;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }
}
