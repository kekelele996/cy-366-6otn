<script setup lang="ts">
import { computed, onMounted, ref, watch } from "vue";
import { showConfirmDialog, showFailToast, showSuccessToast, showToast } from "vant";
import {
  fetchActiveSessions,
  fetchMachines,
  fetchMemberFlows,
  fetchMembers,
  fetchMemberView,
  findOrCreateMember,
  powerOff,
  powerOn,
  purchasePackage,
  rechargeMember,
} from "../api/client";
import type { AccountFlow, CostEstimate, Machine, MachineSession, Member, MemberView } from "../types";
import { formatMinutes, formatMoney } from "../utils/format";
import FlowTable from "./FlowTable.vue";
import MachineBoard from "./MachineBoard.vue";
import MemberPanel from "./MemberPanel.vue";

const members = ref<Member[]>([]);
const currentView = ref<MemberView | null>(null);
const machines = ref<Machine[]>([]);
const flows = ref<AccountFlow[]>([]);
const activeSessions = ref<MachineSession[]>([]);
const selectedMachineId = ref<number | null>(null);
const plannedHours = ref(2);
const busy = ref(false);

function newRequestKey(): string {
  if (typeof crypto !== "undefined" && typeof crypto.randomUUID === "function") {
    return crypto.randomUUID();
  }
  return `req-${Date.now()}-${Math.random().toString(36).slice(2, 10)}`;
}

// 同一组（会员 + 机位 + 时长）的开机意图共用一个幂等键，重复提交不会多扣
let powerOnKey = newRequestKey();
let rechargeKey = newRequestKey();
let purchaseKey = newRequestKey();

const estimate = computed<CostEstimate>(() => {
  const plannedMinutes = plannedHours.value * 60;
  const packageMinutes = Math.min(plannedMinutes, currentView.value?.totalRemainingMinutes ?? 0);
  return {
    packageMinutes,
    balance: Math.max(0, plannedMinutes - packageMinutes) * 0.1,
  };
});

watch([selectedMachineId, plannedHours, () => currentView.value?.member.id], () => {
  powerOnKey = newRequestKey();
});

function messageOf(error: unknown): string {
  return error instanceof Error ? error.message : "操作失败，请稍后重试";
}

async function refreshMembers() {
  members.value = await fetchMembers();
}

async function refreshMachines() {
  machines.value = await fetchMachines();
}

async function refreshActiveSessions() {
  activeSessions.value = await fetchActiveSessions();
}

async function refreshMemberView() {
  if (!currentView.value) {
    return;
  }
  currentView.value = await fetchMemberView(currentView.value.member.id);
}

async function refreshFlows() {
  flows.value = currentView.value ? await fetchMemberFlows(currentView.value.member.id) : [];
}

async function refreshAll() {
  await Promise.all([
    refreshMachines(),
    refreshActiveSessions(),
    refreshMemberView(),
    refreshFlows(),
    refreshMembers(),
  ]);
}

async function handleFindOrCreate(payload: { phone: string; name: string }) {
  busy.value = true;
  try {
    currentView.value = await findOrCreateMember(payload.phone, payload.name);
    showSuccessToast("会员已就绪");
    await Promise.all([refreshMembers(), refreshFlows()]);
  } catch (error) {
    showFailToast(messageOf(error));
  } finally {
    busy.value = false;
  }
}

async function handleSelectMember(memberId: number) {
  busy.value = true;
  try {
    currentView.value = await fetchMemberView(memberId);
    await refreshFlows();
  } catch (error) {
    showFailToast(messageOf(error));
  } finally {
    busy.value = false;
  }
}

async function handleRecharge(amount: number) {
  if (!currentView.value) {
    showToast("请先选择会员");
    return;
  }
  if (!Number.isFinite(amount) || amount <= 0) {
    showToast("请输入正确的充值金额");
    return;
  }
  busy.value = true;
  try {
    currentView.value = await rechargeMember(currentView.value.member.id, amount, rechargeKey);
    rechargeKey = newRequestKey();
    showSuccessToast(`充值成功，余额 ${formatMoney(currentView.value.member.balance)}`);
    await refreshFlows();
  } catch (error) {
    showFailToast(messageOf(error));
  } finally {
    busy.value = false;
  }
}

async function handlePurchase() {
  if (!currentView.value) {
    showToast("请先选择会员");
    return;
  }
  busy.value = true;
  try {
    currentView.value = await purchasePackage(currentView.value.member.id, purchaseKey);
    purchaseKey = newRequestKey();
    showSuccessToast("已购买 10 小时包");
    await refreshFlows();
  } catch (error) {
    showFailToast(messageOf(error));
  } finally {
    busy.value = false;
  }
}

async function handlePowerOn() {
  if (!currentView.value) {
    showToast("请先选择会员");
    return;
  }
  if (selectedMachineId.value === null) {
    showToast("请选择空闲机位");
    return;
  }
  busy.value = true;
  try {
    const result = await powerOn({
      memberId: currentView.value.member.id,
      machineId: selectedMachineId.value,
      plannedHours: plannedHours.value,
      requestId: powerOnKey,
    });
    powerOnKey = newRequestKey();
    selectedMachineId.value = null;
    if (result.replayed) {
      showToast("该开机请求已处理过，未重复扣费");
    } else {
      showSuccessToast(
        `开机成功：扣时长包 ${formatMinutes(result.session.prepaidPackageMinutes)}，扣余额 ${formatMoney(result.session.prepaidAmount)}`,
      );
    }
    await refreshAll();
  } catch (error) {
    showFailToast(messageOf(error));
    await refreshAll();
  } finally {
    busy.value = false;
  }
}

async function handlePowerOff(sessionId: number) {
  try {
    await showConfirmDialog({ title: "停机结算", message: "按实际使用分钟重算，多扣的时长和余额将原路退回。" });
  } catch {
    return;
  }
  busy.value = true;
  try {
    const result = await powerOff(sessionId);
    const session = result.session;
    if (result.replayed) {
      showToast("该上机记录已结算过");
    } else {
      const refundMinutes = Math.max(
        0,
        session.prepaidPackageMinutes - (session.actualPackageMinutes ?? 0),
      );
      showSuccessToast(
        `已停机：实际 ${formatMinutes(session.actualMinutes ?? 0)}，退时长 ${refundMinutes} 分钟，退余额 ${formatMoney(session.refundAmount ?? 0)}`,
      );
    }
    await refreshAll();
  } catch (error) {
    showFailToast(messageOf(error));
    await refreshAll();
  } finally {
    busy.value = false;
  }
}

onMounted(async () => {
  try {
    await Promise.all([refreshMachines(), refreshActiveSessions(), refreshMembers()]);
  } catch {
    showToast("结算服务暂不可用，请确认后端已启动");
  }
});
</script>

<template>
  <section class="work-panel billing-desk">
    <div class="billing-head">
      <h2>会员开机结算</h2>
      <p>先扣最早到期的有效时长包，剩余时段按 6 元/小时扣余额；停机按实际分钟重算，多扣原路退回。</p>
    </div>
    <div class="billing-grid">
      <MemberPanel
        :view="currentView"
        :members="members"
        :busy="busy"
        @find="handleFindOrCreate"
        @select="handleSelectMember"
        @recharge="handleRecharge"
        @purchase="handlePurchase"
      />
      <MachineBoard
        v-model:selected-machine-id="selectedMachineId"
        v-model:planned-hours="plannedHours"
        :machines="machines"
        :active-sessions="activeSessions"
        :busy="busy"
        :member-ready="currentView !== null"
        :estimate="estimate"
        @power-on="handlePowerOn"
        @power-off="handlePowerOff"
      />
    </div>
    <FlowTable :flows="flows" :member-selected="currentView !== null" />
  </section>
</template>
