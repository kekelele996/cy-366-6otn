<script setup lang="ts">
import { computed, ref } from "vue";
import { showConfirmDialog, showToast } from "vant";
import {
  ApiError,
  buyPackage,
  createMember,
  fetchMember,
  rechargeMember,
} from "../api/client";
import type { MemberProfile } from "../types";
import {
  formatDateTime,
  formatMinutes,
  formatMoney,
  transactionLabel,
} from "../utils/format";

const PACKAGE_PRICE = 48;

const phone = ref("");
const profile = ref<MemberProfile | null>(null);
const loading = ref(false);
const busy = ref(false);
const rechargeAmount = ref<number>(20);

const activePackages = computed(() => profile.value?.packages.filter((p) => p.active) ?? []);
const expiredPackages = computed(() => profile.value?.packages.filter((p) => !p.active) ?? []);

function validPhone(): boolean {
  return /^1\d{10}$/.test(phone.value.trim());
}

async function loadProfile(): Promise<void> {
  if (!validPhone()) {
    showToast("请输入 11 位手机号");
    return;
  }
  loading.value = true;
  try {
    profile.value = await fetchMember(phone.value.trim());
  } catch (error) {
    profile.value = null;
    showToast(error instanceof ApiError ? error.message : "查询失败");
  } finally {
    loading.value = false;
  }
}

async function register(): Promise<void> {
  if (!validPhone()) {
    showToast("请输入 11 位手机号");
    return;
  }
  busy.value = true;
  try {
    await createMember(phone.value.trim(), "");
    showToast("会员已建档");
    await loadProfile();
  } catch (error) {
    showToast(error instanceof ApiError ? error.message : "建档失败");
  } finally {
    busy.value = false;
  }
}

async function recharge(): Promise<void> {
  const amount = Number(rechargeAmount.value);
  if (!Number.isFinite(amount) || amount <= 0) {
    showToast("请输入有效的充值金额");
    return;
  }
  busy.value = true;
  try {
    profile.value = await rechargeMember(phone.value.trim(), amount);
    showToast(`已充值 ${formatMoney(amount)}`);
  } catch (error) {
    showToast(error instanceof ApiError ? error.message : "充值失败");
  } finally {
    busy.value = false;
  }
}

async function buyTenHourPackage(): Promise<void> {
  if (!profile.value) {
    return;
  }
  try {
    await showConfirmDialog({
      title: "购买 10 小时时长包",
      message: `将从余额扣款 ${formatMoney(PACKAGE_PRICE)}，购买 600 分钟时长（30 天内有效）。`,
    });
  } catch {
    return;
  }
  busy.value = true;
  try {
    profile.value = await buyPackage(phone.value.trim());
    showToast("10 小时时长包购买成功");
  } catch (error) {
    showToast(error instanceof ApiError ? error.message : "购买失败");
  } finally {
    busy.value = false;
  }
}

function amountClass(amount: number): string {
  return amount < 0 ? "tx-out" : amount > 0 ? "tx-in" : "tx-neutral";
}
</script>

<template>
  <section class="panel member-center">
    <h2>会员中心</h2>
    <div class="phone-row">
      <van-field
        v-model="phone"
        type="tel"
        maxlength="11"
        placeholder="输入会员手机号"
        class="phone-input"
      />
      <van-button type="primary" :loading="loading" @click="loadProfile">查询</van-button>
      <van-button plain type="primary" :disabled="busy" @click="register">建档</van-button>
    </div>

    <div v-if="!profile" class="empty-tip">
      按手机号查询会员；新会员请先「建档」，建档后可充值和购买 10 小时包。
    </div>

    <template v-else>
      <div class="balance-grid">
        <div class="balance-card primary">
          <span class="balance-label">账户余额</span>
          <strong class="balance-value">{{ formatMoney(profile.balance) }}</strong>
        </div>
        <div class="balance-card">
          <span class="balance-label">剩余时长（有效包）</span>
          <strong class="balance-value">{{ formatMinutes(profile.remainingMinutes) }}</strong>
        </div>
        <div class="balance-card">
          <span class="balance-label">累计上机</span>
          <strong class="balance-value">{{ formatMinutes(profile.totalUsageMinutes) }}</strong>
        </div>
      </div>

      <div class="action-row">
        <van-stepper v-model="rechargeAmount" :min="1" :max="10000" :step="10" integer />
        <van-button type="primary" :loading="busy" @click="recharge">充值</van-button>
        <van-button type="warning" plain :loading="busy" @click="buyTenHourPackage">
          购买 10 小时包（¥{{ PACKAGE_PRICE }}）
        </van-button>
      </div>

      <h3>有效时长包（{{ activePackages.length }}）</h3>
      <div v-if="activePackages.length === 0" class="empty-tip">暂无有效时长包，可在上方购买。</div>
      <div v-else class="pkg-list">
        <div v-for="pkg in activePackages" :key="pkg.id" class="pkg-item">
          <span>10 小时包</span>
          <span>剩余 {{ formatMinutes(pkg.remainingMinutes) }}</span>
          <span class="muted">到期 {{ formatDateTime(pkg.expiresAt) }}</span>
        </div>
      </div>

      <h3 v-if="expiredPackages.length > 0">已用完/过期时长包（{{ expiredPackages.length }}）</h3>
      <div v-if="expiredPackages.length > 0" class="pkg-list dimmed">
        <div v-for="pkg in expiredPackages" :key="pkg.id" class="pkg-item">
          <span>10 小时包</span>
          <span>剩余 {{ formatMinutes(pkg.remainingMinutes) }}</span>
          <span class="muted">{{ formatDateTime(pkg.expiresAt) }}</span>
        </div>
      </div>

      <h3>账户流水</h3>
      <div v-if="profile.transactions.length === 0" class="empty-tip">暂无流水。</div>
      <div v-else class="tx-table">
        <div class="tx-row tx-head">
          <span>时间</span><span>类型</span><span>时长</span><span>金额</span><span>余额</span>
        </div>
        <div v-for="tx in profile.transactions" :key="tx.id" class="tx-row">
          <span class="muted">{{ formatDateTime(tx.createdAt) }}</span>
          <span :title="tx.description">{{ transactionLabel(tx.type) }}</span>
          <span>{{ tx.minutes ? formatMinutes(tx.minutes) : "-" }}</span>
          <span :class="amountClass(tx.amount)">{{ tx.amount ? formatMoney(tx.amount) : "-" }}</span>
          <span>{{ formatMoney(tx.balanceAfter) }}</span>
        </div>
      </div>
    </template>
  </section>
</template>
