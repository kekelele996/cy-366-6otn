package com.generated.ldesportsbar.model;

import java.time.LocalDateTime;

public class Session {
  private Long id;
  private Long memberId;
  private Long seatId;
  private String idempotencyKey;
  private Integer plannedMinutes;
  private LocalDateTime startTime;
  private LocalDateTime endTime;
  private Integer actualMinutes;
  private String status;

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

  public Long getSeatId() {
    return seatId;
  }

  public void setSeatId(Long seatId) {
    this.seatId = seatId;
  }

  public String getIdempotencyKey() {
    return idempotencyKey;
  }

  public void setIdempotencyKey(String idempotencyKey) {
    this.idempotencyKey = idempotencyKey;
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

  public LocalDateTime getEndTime() {
    return endTime;
  }

  public void setEndTime(LocalDateTime endTime) {
    this.endTime = endTime;
  }

  public Integer getActualMinutes() {
    return actualMinutes;
  }

  public void setActualMinutes(Integer actualMinutes) {
    this.actualMinutes = actualMinutes;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}
