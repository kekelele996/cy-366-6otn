package com.generated.ldesportsbar.model.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PowerOnRequest(
    @NotNull(message = "请选择会员")
    Long memberId,
    @NotNull(message = "请选择机位")
    Long machineId,
    @NotNull(message = "请选择预计上机时长")
    @Min(value = 1, message = "预计上机至少 1 小时")
    @Max(value = 12, message = "单次开机最长 12 小时")
    Integer plannedHours,
    @NotBlank(message = "缺少请求标识，请刷新页面后重试")
    String requestId
) {
}
