package com.generated.ldesportsbar.model;

import java.time.LocalDateTime;

/** 机位上当前进行中的开机单（空闲机位为 null）。 */
public class SessionBrief {
  private Long sessionId;
  private Long memberId;
  private String phone;
  private String memberName;
  private Integer plannedMinutes;
  private LocalDateTime startTime;

  public Long getSessionId() {
    return sessionId;
  }

  public void setSessionId(Long sessionId) {
    this.sessionId = sessionId;
  }

  public Long getMemberId() {
    return memberId;
  }

  public void setMemberId(Long memberId) {
    this.memberId = memberId;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getMemberName() {
    return memberName;
  }

  public void setMemberName(String memberName) {
    this.memberName = memberName;
  }

  public Integer getPlannedMinutes() {
    return plannedMinutes;
  }

  public void setPlannedMinutes(Integer plannedMinutes) {
    this.plannedMinutes = plannedMinutes;
  }

  public LocalDateTime getStartTime() {
    return startTime;
  }

  public void setStartTime(LocalDateTime startTime) {
    this.startTime = startTime;
  }
}
