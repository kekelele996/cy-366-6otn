package com.generated.ldesportsbar.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.generated.ldesportsbar.dto.StartSessionRequest;
import com.generated.ldesportsbar.model.StartSessionResult;
import com.generated.ldesportsbar.model.StopSessionResult;
import com.generated.ldesportsbar.service.SessionService;

@RestController
public class SessionController {

  private final SessionService sessionService;

  public SessionController(SessionService sessionService) {
    this.sessionService = sessionService;
  }

  /**
   * 开机：选择空闲机位 + 手机号 + 预计时长，先扣时长包再扣余额。
   * 同 idempotencyKey 重复提交不会多扣。
   */
  @PostMapping({"/api/sessions/start", "/sessions/start"})
  public StartSessionResult start(@Valid @RequestBody StartSessionRequest request) {
    return sessionService.start(request.getPhone(), request.getSeatId(),
      request.getPlannedMinutes(), request.getIdempotencyKey());
  }

  /** 停机：按实际使用分钟重算，多扣的时长/余额原路退回。 */
  @PostMapping({"/api/sessions/{id}/stop", "/sessions/{id}/stop"})
  public StopSessionResult stop(@PathVariable Long id) {
    return sessionService.stop(id);
  }
}
