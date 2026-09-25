package com.generated.ldesportsbar.model;

/** 机位视图，附带当前使用中的开机单。 */
public record SeatView(
  Long id,
  String seatNo,
  String zone,
  String status,
  SessionBrief activeSession
) {
}
