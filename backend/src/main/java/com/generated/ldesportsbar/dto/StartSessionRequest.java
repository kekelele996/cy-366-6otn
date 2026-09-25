package com.generated.ldesportsbar.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class StartSessionRequest {
  @NotBlank(message = "手机号不能为空")
  @Pattern(regexp = "1\\d{10}", message = "请输入 11 位手机号")
  private String phone;

  @NotNull(message = "机位不能为空")
  private Long seatId;

  @NotNull(message = "预计使用时长不能为空")
  @Min(value = 1, message = "预计使用时长至少 1 分钟")
  @Max(value = 600, message = "单次开机最长 600 分钟")
  private Integer plannedMinutes;

  /** 幂等键：前端对同一次开机提交保持不变，重复提交不会多扣。 */
  @NotBlank(message = "请求标识不能为空")
  @Size(max = 64, message = "请求标识最长 64 个字符")
  private String idempotencyKey;

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public Long getSeatId() {
    return seatId;
  }

  public void setSeatId(Long seatId) {
    this.seatId = seatId;
  }

  public Integer getPlannedMinutes() {
    return plannedMinutes;
  }

  public void setPlannedMinutes(Integer plannedMinutes) {
    this.plannedMinutes = plannedMinutes;
  }

  public String getIdempotencyKey() {
    return idempotencyKey;
  }

  public void setIdempotencyKey(String idempotencyKey) {
    this.idempotencyKey = idempotencyKey;
  }
}
