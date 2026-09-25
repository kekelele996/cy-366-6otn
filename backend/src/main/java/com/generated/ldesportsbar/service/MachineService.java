package com.generated.ldesportsbar.service;

import com.generated.ldesportsbar.mapper.MachineMapper;
import com.generated.ldesportsbar.model.entity.Machine;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class MachineService {
  private final MachineMapper machineMapper;

  public MachineService(MachineMapper machineMapper) {
    this.machineMapper = machineMapper;
  }

  public List<Machine> listMachines() {
    return machineMapper.findAll();
  }
}
