package com.generated.ldesportsbar.model.entity;

public class Machine {
  private Long id;
  private String code;
  private String zone;
  private String status;

  public Machine() {
  }

  public Machine(String code, String zone, String status) {
    this.code = code;
    this.zone = zone;
    this.status = status;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getZone() {
    return zone;
  }

  public void setZone(String zone) {
    this.zone = zone;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}
