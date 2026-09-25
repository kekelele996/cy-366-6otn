import { localFeatures, localKpis, operationRecords } from "../data/workbench";
import type { OverviewResponse } from "../types";
import { APP_CODE, APP_NAME } from "../constants/app";

export function createFallbackOverview(): OverviewResponse {
  return {
    appName: APP_NAME,
    appCode: APP_CODE,
    description: "面向电竞馆/网咖门店，提供机位状态监控、会员充值消费和赛事管理的运营工具。",
    features: localFeatures,
    kpis: localKpis,
    records: operationRecords,
  };
}
