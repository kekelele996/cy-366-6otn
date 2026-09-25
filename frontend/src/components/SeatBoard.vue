<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref } from "vue";
import { showConfirmDialog, showToast } from "vant";
import { ApiError, fetchSeats, startSession, stopSession } from "../api/client";
import type { SeatView, StopSessionResult } from "../types";
import { createIdempotencyKey, formatDateTime, formatMinutes, formatMoney } from "../utils/format";

const seats = ref<SeatView[]>([]);
const loading = ref(false);
const zoneFilter = ref<string>("ALL");

const startPhone = ref("");
const startMinutes = ref<number>(60);
const showStartDialog = ref(false);
const starting = ref(false);
const idempotencyKey = ref<string>("");
const startSeat = ref<SeatView | null>(null);

const stopping = ref<number | null>(null);

let timer: ReturnType<typeof setInterval> | null = null;

const zones = computed(() => {
  const set = new Set(seats.value.map((s) => s.zone));
  return ["ALL", ...Array.from(set)];
});

const filteredSeats = computed(() =>
  zoneFilter.value === "ALL" ? seats.value : seats.value.filter((s) => s.zone === zoneFilter.value),
);

const idleCount = computed(() => seats.value.filter((s) => s.status === "IDLE").length);
const inUseCount = computed(() => seats.value.filter((s) => s.status === "IN_USE").length);
const brokenCount = computed(() => seats.value.filter((s) => s.status === "BROKEN").length);

async function loadSeats(): Promise<void> {
  loading.value = true;
  try {
    seats.value = await fetchSeats();
  } catch (error) {
    showToast(error instanceof ApiError ? error.message : "机位加载失败");
  } finally {
    loading.value = false;
  }
}

function seatClass(seat: SeatView): string {
  return ["seat-card", `seat-${seat.status.toLowerCase()}`].join(" ");
}

function statusLabel(status: SeatView["status"]): string {
  if (status === "IDLE") return "空闲";
  if (status === "IN_USE") return "使用中";
  return "故障";
}

function openStartDialog(seat: SeatView): void {
  if (seat.status === "BROKEN") {
    showToast("该机位故障中，暂不能开机");
    return;
  }
  if (seat.status === "IN_USE") {
    showToast("该机位正在使用中");
    return;
  }
  startSeat.value = seat;
  startPhone.value = "";
  startMinutes.value = 60;
  showStartDialog.value = true;
  // 为这次开机生成稳定的请求标识：重试/重复提交都沿用同一个键，后端不会多扣
  idempotencyKey.value = createIdempotencyKey();
}

async function confirmStart(): Promise<void> {
  if (!startSeat.value) {
    return;
  }
  if (!/^1\d{10}$/.test(startPhone.value.trim())) {
    showToast("请输入 11 位会员手机号");
    return;
  }
  const minutes = Number(startMinutes.value);
  if (!Number.isFinite(minutes) || minutes < 1) {
    showToast("请输入有效的预计分钟数");
    return;
  }
  starting.value = true;
  try {
    const result = await startSession({
      phone: startPhone.value.trim(),
      seatId: startSeat.value.id,
      plannedMinutes: minutes,
      idempotencyKey: idempotencyKey.value,
    });
    const replay = result.idempotentReplay ? "（重复请求已识别，未重复扣费）" : "";
    showToast(
      `机位 ${result.seatNo} 开机成功${replay}：时长包扣 ${formatMinutes(result.packageMinutesUsed)}`
      + `，余额扣 ${formatMoney(result.balanceCharged)}`,
    );
    startSeat.value = null;
    showStartDialog.value = false;
    await loadSeats();
  } catch (error) {
    showToast(error instanceof ApiError ? error.message : "开机失败");
  } finally {
    starting.value = false;
  }
}

async function confirmStop(seat: SeatView): Promise<void> {
  if (!seat.activeSession) {
    return;
  }
  try {
    await showConfirmDialog({
      title: `停机结算 · ${seat.seatNo}`,
      message: `会员 ${seat.activeSession.phone}，预计 ${formatMinutes(seat.activeSession.plannedMinutes)}。`
        + "停机后将按实际使用分钟重算，多扣的时长/余额原路退回。",
    });
  } catch {
    return;
  }
  stopping.value = seat.activeSession.sessionId;
  try {
    const result: StopSessionResult = await stopSession(seat.activeSession.sessionId);
    showToast(
      `已停机：实际 ${formatMinutes(result.actualMinutes)}，时长包 ${formatMinutes(result.packageMinutesUsed)}`
      + (result.packageMinutesRefunded ? `，退时长 ${formatMinutes(result.packageMinutesRefunded)}` : "")
      + (result.balanceRefunded > 0 ? `，退余额 ${formatMoney(result.balanceRefunded)}` : "")
      + `，当前余额 ${formatMoney(result.balanceAfter)}`,
    );
    await loadSeats();
  } catch (error) {
    showToast(error instanceof ApiError ? error.message : "停机失败");
  } finally {
    stopping.value = null;
  }
}

onMounted(() => {
  void loadSeats();
  timer = setInterval(() => void loadSeats(), 10000);
});

onUnmounted(() => {
  if (timer) {
    clearInterval(timer);
  }
});
</script>

<template>
  <section class="panel seat-board">
    <div class="board-head">
      <h2>机位看板</h2>
      <div class="seat-summary">
        <van-tag type="success">空闲 {{ idleCount }}</van-tag>
        <van-tag type="primary">使用中 {{ inUseCount }}</van-tag>
        <van-tag type="danger">故障 {{ brokenCount }}</van-tag>
        <van-button size="small" plain :loading="loading" @click="loadSeats">刷新</van-button>
      </div>
    </div>

    <van-tabs v-model:active="zoneFilter" sticky offset-top="0">
      <van-tab v-for="zone in zones" :key="zone" :name="zone" :title="zone === 'ALL' ? '全部区域' : zone" />
    </van-tabs>

    <div class="seat-grid">
      <button
        v-for="seat in filteredSeats"
        :key="seat.id"
        type="button"
        :class="seatClass(seat)"
        @click="openStartDialog(seat)"
      >
        <span class="seat-no">{{ seat.seatNo }}</span>
        <span class="seat-zone">{{ seat.zone }}</span>
        <span class="seat-status">{{ statusLabel(seat.status) }}</span>
        <template v-if="seat.activeSession">
          <span class="seat-member">{{ seat.activeSession.memberName || seat.activeSession.phone }}</span>
          <span class="seat-since">开机 {{ formatDateTime(seat.activeSession.startTime) }}</span>
        </template>
      </button>
    </div>

    <div class="inuse-list">
      <h3>使用中机位（点击可停机结算）</h3>
      <div v-if="inUseCount === 0" class="empty-tip">当前没有进行中的开机单。</div>
      <div
        v-for="seat in seats.filter((s) => s.status === 'IN_USE')"
        :key="`stop-${seat.id}`"
        class="inuse-item"
      >
        <div>
          <strong>{{ seat.seatNo }}</strong>
          <span class="muted">（{{ seat.zone }}）</span>
          <template v-if="seat.activeSession">
            <span class="muted"> · {{ seat.activeSession.phone }} · 预计
              {{ formatMinutes(seat.activeSession.plannedMinutes) }} ·
              开机 {{ formatDateTime(seat.activeSession.startTime) }}</span>
          </template>
        </div>
        <van-button
          size="small"
          type="danger"
          plain
          :loading="stopping === seat.activeSession?.sessionId"
          @click="confirmStop(seat)"
        >停机结算</van-button>
      </div>
    </div>

    <van-dialog
      v-model="showStartDialog"
      title="开机"
      :show-confirm="false"
      @closed="startSeat = null"
    >
      <div class="start-form">
        <p class="muted">机位：{{ startSeat?.seatNo }}（{{ startSeat?.zone }}）</p>
        <van-field v-model="startPhone" type="tel" maxlength="11" label="手机号" placeholder="会员手机号" />
        <van-field v-model="startMinutes" type="number" label="预计分钟" placeholder="如 60">
          <template #button>
            <span class="muted">6 元/小时，先扣时长包</span>
          </template>
        </van-field>
        <p class="muted tiny">重复提交同一开机请求不会多扣（请求标识：{{ idempotencyKey.slice(0, 12) }}…）</p>
        <div class="start-actions">
          <van-button block plain @click="showStartDialog = false">取消</van-button>
          <van-button block type="primary" :loading="starting" @click="confirmStart">确认开机</van-button>
        </div>
      </div>
    </van-dialog>
  </section>
</template>
