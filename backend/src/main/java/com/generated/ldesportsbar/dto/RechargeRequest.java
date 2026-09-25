package com.generated.ldesportsbar.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class RechargeRequest {
  @NotNull(message = "充值金额不能为空")
  @DecimalMin(value = "0.01", message = "充值金额至少 0.01 元")
  @DecimalMax(value = "10000.00", message = "单次充值不能超过 10000 元")
  private BigDecimal amount;

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }
}
