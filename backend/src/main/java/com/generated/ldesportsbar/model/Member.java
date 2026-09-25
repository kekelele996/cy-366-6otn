package com.generated.ldesportsbar.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Member {
  private Long id;
  private String phone;
  private String name;
  private BigDecimal balance;
  private Integer totalUsageMinutes;
  private LocalDateTime createdAt;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public BigDecimal getBalance() {
    return balance;
  }

  public void setBalance(BigDecimal balance) {
    this.balance = balance;
  }

  public Integer getTotalUsageMinutes() {
    return totalUsageMinutes;
  }

  public void setTotalUsageMinutes(Integer totalUsageMinutes) {
    this.totalUsageMinutes = totalUsageMinutes;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }
}
