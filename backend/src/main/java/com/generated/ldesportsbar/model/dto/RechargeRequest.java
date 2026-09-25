package com.generated.ldesportsbar.model.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record RechargeRequest(
    @NotNull(message = "充值金额不能为空")
    @DecimalMin(value = "0.01", message = "充值金额需大于 0")
    BigDecimal amount,
    String requestId
) {
}
