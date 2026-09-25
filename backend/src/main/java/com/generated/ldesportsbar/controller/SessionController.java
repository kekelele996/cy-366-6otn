package com.generated.ldesportsbar.controller;

import com.generated.ldesportsbar.model.dto.PowerOffRequest;
import com.generated.ldesportsbar.model.dto.PowerOnRequest;
import com.generated.ldesportsbar.model.dto.SessionResult;
import com.generated.ldesportsbar.model.entity.MachineSession;
import com.generated.ldesportsbar.service.SessionService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SessionController {
  private final SessionService sessionService;

  public SessionController(SessionService sessionService) {
    this.sessionService = sessionService;
  }

  @PostMapping({"/sessions/power-on", "/api/sessions/power-on"})
  public SessionResult powerOn(@Valid @RequestBody PowerOnRequest request) {
    return sessionService.powerOn(request);
  }

  @PostMapping({"/sessions/power-off", "/api/sessions/power-off"})
  public SessionResult powerOff(@Valid @RequestBody PowerOffRequest request) {
    return sessionService.powerOff(request.sessionId());
  }

  @GetMapping({"/sessions/active", "/api/sessions/active"})
  public List<MachineSession> active() {
    return sessionService.listActive();
  }
}
