<script setup lang="ts">
import { onMounted, ref } from "vue";
import { fetchOverview } from "./api/client";
import { APP_CODE, APP_NAME } from "./constants/app";
import { REQUEST_MESSAGES } from "./constants/messages";
import { createFallbackOverview } from "./state/dashboard";
import type { OverviewResponse } from "./types";
import FeatureStrip from "./components/FeatureStrip.vue";
import MetricGrid from "./components/MetricGrid.vue";
import OperationsTable from "./components/OperationsTable.vue";
import SeatBoard from "./components/SeatBoard.vue";
import MemberCenter from "./components/MemberCenter.vue";

const active = ref<"overview" | "seats" | "members">("seats");
const overview = ref<OverviewResponse>(createFallbackOverview());
const notice = ref(REQUEST_MESSAGES.overviewFallback);

onMounted(async () => {
  try {
    overview.value = await fetchOverview();
    notice.value = "后端服务已联通，当前展示实时接口数据。";
  } catch {
    notice.value = REQUEST_MESSAGES.overviewFallback;
  }
});
</script>

<template>
  <main class="app-shell">
    <header class="topbar">
      <div>
        <span class="brand-code">{{ APP_CODE }}</span>
        <h1 class="brand-title">{{ APP_NAME }}</h1>
      </div>
      <span class="pill">会员开机结算已接通 · 6 元/小时</span>
    </header>

    <section class="workspace">
      <template v-if="active === 'overview'">
        <div class="lead-grid">
          <article class="hero-panel">
            <span class="pill">{{ notice }}</span>
            <h2>{{ overview.appName }}</h2>
            <p>{{ overview.description }}</p>
          </article>
          <MetricGrid :items="overview.kpis" />
        </div>
        <FeatureStrip :items="overview.features" />
        <section class="work-panel">
          <h2>运营任务流</h2>
          <OperationsTable :records="overview.records" />
        </section>
      </template>

      <SeatBoard v-else-if="active === 'seats'" />
      <MemberCenter v-else />
    </section>

    <van-tabbar v-model="active" placeholder safe-area-inset-bottom>
      <van-tabbar-item name="overview" icon="chart-trending-o">运营总览</van-tabbar-item>
      <van-tabbar-item name="seats" icon="desktop-o">机位看板</van-tabbar-item>
      <van-tabbar-item name="members" icon="contact">会员中心</van-tabbar-item>
    </van-tabbar>
  </main>
</template>
