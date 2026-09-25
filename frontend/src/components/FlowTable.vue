<script setup lang="ts">
import type { AccountFlow } from "../types";
import { formatDateTime, formatMoney, formatSignedMinutes, formatSignedMoney } from "../utils/format";

defineProps<{ flows: AccountFlow[]; memberSelected: boolean }>();

const FLOW_LABELS: Record<string, string> = {
  RECHARGE: "余额充值",
  PACKAGE_PURCHASE: "购买时长包",
  PACKAGE_DEDUCT: "扣时长包",
  BALANCE_DEDUCT: "扣余额",
  PACKAGE_REFUND: "退时长包",
  BALANCE_REFUND: "退余额",
};

function flowLabel(type: string): string {
  return FLOW_LABELS[type] ?? type;
}
</script>

<template>
  <div class="flow-section">
    <h3>账户流水</h3>
    <div v-if="!memberSelected" class="empty-hint">选择会员后展示余额、时长变动流水。</div>
    <div v-else-if="flows.length === 0" class="empty-hint">暂无流水记录。</div>
    <div v-else class="plain-table flow-table" role="table" aria-label="账户流水">
      <div class="plain-row plain-head flow-row" role="row">
        <span>时间</span>
        <span>类型</span>
        <span>金额</span>
        <span>时长</span>
        <span>余额</span>
        <span>备注</span>
      </div>
      <div v-for="flow in flows" :key="flow.id" class="plain-row flow-row" role="row">
        <span>{{ formatDateTime(flow.createdAt) }}</span>
        <span>{{ flowLabel(flow.type) }}</span>
        <span :class="{ 'amount-in': flow.amount > 0, 'amount-out': flow.amount < 0 }">
          {{ formatSignedMoney(flow.amount) }}
        </span>
        <span :class="{ 'amount-in': flow.minutes > 0, 'amount-out': flow.minutes < 0 }">
          {{ formatSignedMinutes(flow.minutes) }}
        </span>
        <span>{{ formatMoney(flow.balanceAfter) }}</span>
        <span>{{ flow.remark || "—" }}</span>
      </div>
    </div>
  </div>
</template>
