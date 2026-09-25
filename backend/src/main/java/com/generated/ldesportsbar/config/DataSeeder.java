package com.generated.ldesportsbar.config;

import com.generated.ldesportsbar.mapper.MachineMapper;
import com.generated.ldesportsbar.model.entity.Machine;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/** 首次启动时初始化机位数据（幂等：已有数据则跳过） */
@Component
public class DataSeeder implements CommandLineRunner {
  private final MachineMapper machineMapper;

  public DataSeeder(MachineMapper machineMapper) {
    this.machineMapper = machineMapper;
  }

  @Override
  public void run(String... args) {
    if (machineMapper.count() > 0) {
      return;
    }
    for (int i = 1; i <= 6; i++) {
      machineMapper.insert(new Machine(String.format("A%02d", i), "A区", BillingConstants.MACHINE_IDLE));
    }
    for (int i = 1; i <= 6; i++) {
      machineMapper.insert(new Machine(String.format("B%02d", i), "B区", BillingConstants.MACHINE_IDLE));
    }
    // 演示一台故障机位
    machineMapper.insert(new Machine("B07", "B区", "FAULT"));
  }
}
