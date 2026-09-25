package com.generated.ldesportsbar.model.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MachineSession {
  private Long id;
  private String requestId;
  private Long memberId;
  private Long machineId;
  private Integer plannedMinutes;
  private Integer prepaidPackageMinutes;
  private BigDecimal prepaidAmount;
  private Integer actualMinutes;
  private Integer actualPackageMinutes;
  private BigDecimal actualAmount;
  private BigDecimal refundAmount;
  private String status;
  private LocalDateTime startedAt;
  private LocalDateTime endedAt;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getRequestId() {
    return requestId;
  }

  public void setRequestId(String requestId) {
    this.requestId = requestId;
  }

  public Long getMemberId() {
    return memberId;
  }

  public void setMemberId(Long memberId) {
    this.memberId = memberId;
  }

  public Long getMachineId() {
    return machineId;
  }

  public void setMachineId(Long machineId) {
    this.machineId = machineId;
  }

  public Integer getPlannedMinutes() {
    return plannedMinutes;
  }

  public void setPlannedMinutes(Integer plannedMinutes) {
    this.plannedMinutes = plannedMinutes;
  }

  public Integer getPrepaidPackageMinutes() {
    return prepaidPackageMinutes;
  }

  public void setPrepaidPackageMinutes(Integer prepaidPackageMinutes) {
    this.prepaidPackageMinutes = prepaidPackageMinutes;
  }

  public BigDecimal getPrepaidAmount() {
    return prepaidAmount;
  }

  public void setPrepaidAmount(BigDecimal prepaidAmount) {
    this.prepaidAmount = prepaidAmount;
  }

  public Integer getActualMinutes() {
    return actualMinutes;
  }

  public void setActualMinutes(Integer actualMinutes) {
    this.actualMinutes = actualMinutes;
  }

  public Integer getActualPackageMinutes() {
    return actualPackageMinutes;
  }

  public void setActualPackageMinutes(Integer actualPackageMinutes) {
    this.actualPackageMinutes = actualPackageMinutes;
  }

  public BigDecimal getActualAmount() {
    return actualAmount;
  }

  public void setActualAmount(BigDecimal actualAmount) {
    this.actualAmount = actualAmount;
  }

  public BigDecimal getRefundAmount() {
    return refundAmount;
  }

  public void setRefundAmount(BigDecimal refundAmount) {
    this.refundAmount = refundAmount;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public LocalDateTime getStartedAt() {
    return startedAt;
  }

  public void setStartedAt(LocalDateTime startedAt) {
    this.startedAt = startedAt;
  }

  public LocalDateTime getEndedAt() {
    return endedAt;
  }

  public void setEndedAt(LocalDateTime endedAt) {
    this.endedAt = endedAt;
  }
}
