package com.generated.ldesportsbar.model.dto;

import jakarta.validation.constraints.NotNull;

public record PowerOffRequest(
    @NotNull(message = "缺少上机记录")
    Long sessionId
) {
}
