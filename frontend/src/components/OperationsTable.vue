<script setup lang="ts">
import type { OperationRecord } from "../types";

defineProps<{ records: OperationRecord[] }>();
</script>

<template>
  <div class="plain-table" role="table" aria-label="运营任务">
    <div class="plain-row plain-head" role="row">
      <span>模块</span>
      <span>负责人</span>
      <span>状态</span>
      <span>指标</span>
    </div>
    <div v-for="record in records" :key="record.key" class="plain-row" role="row">
      <span>{{ record.name }}</span>
      <span>{{ record.owner }}</span>
      <span>{{ record.status }}</span>
      <span>{{ record.metric }}</span>
    </div>
  </div>
</template>
