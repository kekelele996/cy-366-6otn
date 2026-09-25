<script setup lang="ts">
import { ref } from "vue";
import type { Member, MemberView } from "../types";
import { formatDateTime, formatMinutes, formatMoney } from "../utils/format";

defineProps<{ view: MemberView | null; members: Member[]; busy: boolean }>();

const emit = defineEmits<{
  find: [payload: { phone: string; name: string }];
  select: [memberId: number];
  recharge: [amount: number];
  purchase: [];
}>();

const phone = ref("");
const name = ref("");
const rechargeAmount = ref("100");

const PACKAGE_HOURS = 10;
const PACKAGE_PRICE = 50;

const PACKAGE_STATUS: Record<string, { label: string; type: "success" | "primary" | "warning" }> = {
  ACTIVE: { label: "有效", type: "success" },
  DEPLETED: { label: "已用完", type: "primary" },
  EXPIRED: { label: "已过期", type: "warning" },
};

function submitFind() {
  emit("find", { phone: phone.value.trim(), name: name.value.trim() });
}

function submitRecharge() {
  emit("recharge", Number(rechargeAmount.value));
}
</script>

<template>
  <div class="member-panel">
    <h3>会员账户</h3>
    <div class="find-form">
      <van-field v-model="phone" type="tel" maxlength="11" placeholder="手机号（11 位）" />
      <van-field v-model="name" maxlength="20" placeholder="昵称（选填）" />
      <van-button type="primary" block :loading="busy" @click="submitFind">建档 / 查找</van-button>
    </div>

    <div v-if="members.length" class="member-chips">
      <button
        v-for="item in members.slice(0, 8)"
        :key="item.id"
        class="member-chip"
        :class="{ active: view?.member.id === item.id }"
        type="button"
        @click="emit('select', item.id)"
      >
        {{ item.name || item.phone }}
      </button>
    </div>

    <template v-if="view">
      <div class="balance-cards">
        <div class="balance-card">
          <span>账户余额</span>
          <strong>{{ formatMoney(view.member.balance) }}</strong>
        </div>
        <div class="balance-card">
          <span>剩余时长</span>
          <strong>{{ formatMinutes(view.totalRemainingMinutes) }}</strong>
        </div>
      </div>
      <p class="member-meta">{{ view.member.phone }}<template v-if="view.member.name"> · {{ view.member.name }}</template></p>

      <div class="recharge-row">
        <van-field v-model="rechargeAmount" type="number" placeholder="充值金额（元）" />
        <van-button type="primary" :loading="busy" @click="submitRecharge">充值</van-button>
      </div>
      <van-button class="package-button" plain type="primary" block :loading="busy" @click="emit('purchase')">
        购买 {{ PACKAGE_HOURS }} 小时包（{{ formatMoney(PACKAGE_PRICE) }}，30 天有效）
      </van-button>

      <div v-if="view.packages.length" class="package-list">
        <div v-for="pkg in view.packages" :key="pkg.id" class="package-item">
          <span>时长包 #{{ pkg.id }}</span>
          <span>剩 {{ formatMinutes(pkg.remainingMinutes) }} / 共 {{ formatMinutes(pkg.totalMinutes) }}</span>
          <span>{{ formatDateTime(pkg.expiresAt).slice(0, 10) }} 到期</span>
          <van-tag :type="PACKAGE_STATUS[pkg.status].type">{{ PACKAGE_STATUS[pkg.status].label }}</van-tag>
        </div>
      </div>
    </template>
    <p v-else class="empty-hint">输入手机号建档，或点击上方会员开始操作。</p>
  </div>
</template>
