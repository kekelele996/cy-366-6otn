<script setup lang="ts">
import { computed } from "vue";
import type { CostEstimate, Machine, MachineSession } from "../types";
import { formatDateTime, formatMinutes, formatMoney } from "../utils/format";

const props = defineProps<{
  machines: Machine[];
  activeSessions: MachineSession[];
  selectedMachineId: number | null;
  plannedHours: number;
  busy: boolean;
  memberReady: boolean;
  estimate: CostEstimate;
}>();

const emit = defineEmits<{
  "update:selectedMachineId": [value: number | null];
  "update:plannedHours": [value: number];
  powerOn: [];
  powerOff: [sessionId: number];
}>();

const MACHINE_STATUS: Record<string, { label: string; type: "success" | "warning" | "danger" }> = {
  IDLE: { label: "空闲", type: "success" },
  IN_USE: { label: "使用中", type: "warning" },
  FAULT: { label: "故障", type: "danger" },
};

const machinesByZone = computed(() => {
  const groups = new Map<string, Machine[]>();
  for (const machine of props.machines) {
    const list = groups.get(machine.zone) ?? [];
    list.push(machine);
    groups.set(machine.zone, list);
  }
  return [...groups.entries()];
});

const sessionByMachine = computed(() => {
  const map = new Map<number, MachineSession>();
  for (const session of props.activeSessions) {
    map.set(session.machineId, session);
  }
  return map;
});

const canPowerOn = computed(
  () => props.memberReady && props.selectedMachineId !== null && !props.busy,
);

function clickMachine(machine: Machine) {
  if (machine.status !== "IDLE") {
    return;
  }
  emit("update:selectedMachineId", machine.id === props.selectedMachineId ? null : machine.id);
}

function onHoursChange(value: string | number) {
  emit("update:plannedHours", Number(value));
}
</script>

<template>
  <div class="machine-panel">
    <h3>机位看板</h3>
    <div v-for="[zone, zoneMachines] in machinesByZone" :key="zone" class="zone-group">
      <span class="zone-name">{{ zone }}</span>
      <div class="machine-grid">
        <div
          v-for="machine in zoneMachines"
          :key="machine.id"
          class="machine-card"
          :class="[
            `machine-${machine.status.toLowerCase().replace('_', '-')}`,
            { selected: machine.id === selectedMachineId },
          ]"
          @click="clickMachine(machine)"
        >
          <div class="machine-head">
            <strong>{{ machine.code }}</strong>
            <van-tag :type="MACHINE_STATUS[machine.status].type">
              {{ MACHINE_STATUS[machine.status].label }}
            </van-tag>
          </div>
          <template v-if="machine.status === 'IN_USE' && sessionByMachine.get(machine.id)">
            <p class="machine-session">
              {{ formatDateTime(sessionByMachine.get(machine.id)!.startedAt).slice(5, 16) }} 开机
              · 预计 {{ formatMinutes(sessionByMachine.get(machine.id)!.plannedMinutes) }}
            </p>
            <van-button
              size="small"
              type="warning"
              :loading="busy"
              @click.stop="emit('powerOff', sessionByMachine.get(machine.id)!.id)"
            >
              停机结算
            </van-button>
          </template>
        </div>
      </div>
    </div>

    <div class="power-on-bar">
      <div class="hours-picker">
        <span>预计上机</span>
        <van-stepper
          :model-value="plannedHours"
          min="1"
          max="12"
          @update:model-value="onHoursChange"
        />
        <span>小时</span>
      </div>
      <p class="estimate">
        预计扣时长包 {{ formatMinutes(estimate.packageMinutes) }} + 余额 {{ formatMoney(estimate.balance) }}
      </p>
      <van-button type="primary" block :disabled="!canPowerOn" :loading="busy" @click="emit('powerOn')">
        开机结算
      </van-button>
    </div>
  </div>
</template>
