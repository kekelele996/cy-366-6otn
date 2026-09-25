package com.generated.ldesportsbar.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CreateMemberRequest {
  @NotBlank(message = "手机号不能为空")
  @Pattern(regexp = "1\\d{10}", message = "请输入 11 位手机号")
  private String phone;

  @Size(max = 60, message = "姓名最长 60 个字符")
  private String name;

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
}
