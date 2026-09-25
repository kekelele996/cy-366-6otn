package com.generated.ldesportsbar.service;

import java.util.List;
import com.generated.ldesportsbar.mapper.SeatMapper;
import com.generated.ldesportsbar.model.Seat;
import com.generated.ldesportsbar.model.SeatView;
import com.generated.ldesportsbar.model.SessionBrief;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SeatService {

  private final SeatMapper seatMapper;

  public SeatService(SeatMapper seatMapper) {
    this.seatMapper = seatMapper;
  }

  @Transactional(readOnly = true)
  public List<SeatView> listSeats() {
    return seatMapper.findAll().stream().map(this::toView).toList();
  }

  @Transactional(readOnly = true)
  public List<String> listZones() {
    return seatMapper.findZones();
  }

  private SeatView toView(Seat seat) {
    List<SessionBrief> briefs = seatMapper.findActiveBriefs(seat.getId());
    return new SeatView(seat.getId(), seat.getSeatNo(), seat.getZone(), seat.getStatus(),
      briefs.isEmpty() ? null : briefs.get(0));
  }
}
