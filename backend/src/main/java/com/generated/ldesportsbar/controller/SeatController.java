package com.generated.ldesportsbar.controller;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.generated.ldesportsbar.model.SeatView;
import com.generated.ldesportsbar.service.SeatService;

@RestController
@RequestMapping({"/api/seats", "/seats"})
public class SeatController {

  private final SeatService seatService;

  public SeatController(SeatService seatService) {
    this.seatService = seatService;
  }

  /** 机位看板：全部机位、状态及当前使用中的开机单。 */
  @GetMapping
  public List<SeatView> listSeats() {
    return seatService.listSeats();
  }
}
