package com.generated.ldesportsbar.controller;

import com.generated.ldesportsbar.model.entity.Machine;
import com.generated.ldesportsbar.service.MachineService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MachineController {
  private final MachineService machineService;

  public MachineController(MachineService machineService) {
    this.machineService = machineService;
  }

  @GetMapping({"/machines", "/api/machines"})
  public List<Machine> list() {
    return machineService.listMachines();
  }
}
