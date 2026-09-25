import type { FeatureItem, KpiItem, OperationRecord } from "../types";

export const localFeatures: FeatureItem[] = [
  {
    "id": 1,
    "title": "机位/包厢实时状态看板",
    "description": "以网格或列表形式展示所有机位/包厢的实时状态（空闲/使用中/故障/预约），支持按区域筛选，点击可查看详情。",
    "status": "已上线",
    "metric": "88%"
  },
  {
    "id": 2,
    "title": "会员充值与时长包",
    "description": "会员账户支持充值余额，购买时长包（如10小时/30小时/月卡），消费时优先扣除时长包余额，不足时扣余额。",
    "status": "排期中",
    "metric": "31 单"
  },
  {
    "id": 3,
    "title": "机位预约与续费",
    "description": "会员可提前预约指定机位和时段，到店扫码开机，使用过程中可续费延长上机时间，快到期前提醒续费。",
    "status": "巡检中",
    "metric": "10 项"
  },
  {
    "id": 4,
    "title": "上机时长排行榜",
    "description": "记录会员累计上机时长，生成日/周/月排行榜，激励高频玩家，支持按游戏类型分类统计。",
    "status": "优化中",
    "metric": "4 级"
  },
  {
    "id": 5,
    "title": "赛事报名与战队管理",
    "description": "门店发布电竞赛事（如LOL/CSGO/王者荣耀），玩家以个人或战队形式报名，系统自动抽签分组，记录比赛结果和战绩。",
    "status": "可导出",
    "metric": "28 条"
  }
];

export const localKpis: KpiItem[] = [
  {
    "label": "今日处理",
    "value": "106",
    "trend": "+12%",
    "tone": "primary"
  },
  {
    "label": "预约/订单",
    "value": "43",
    "trend": "+8%",
    "tone": "warm"
  },
  {
    "label": "履约率",
    "value": "91%",
    "trend": "+3%",
    "tone": "cool"
  },
  {
    "label": "待处理",
    "value": "10",
    "trend": "需跟进",
    "tone": "neutral"
  }
];

export const operationRecords: OperationRecord[] = [
  {
    "key": "ldesportsbar-1",
    "name": "机位/包厢实时状态看板",
    "owner": "运营组",
    "status": "已上线",
    "metric": "88%",
    "priority": "高"
  },
  {
    "key": "ldesportsbar-2",
    "name": "会员充值与时长包",
    "owner": "管理员",
    "status": "排期中",
    "metric": "31 单",
    "priority": "中"
  },
  {
    "key": "ldesportsbar-3",
    "name": "机位预约与续费",
    "owner": "服务台",
    "status": "巡检中",
    "metric": "10 项",
    "priority": "低"
  },
  {
    "key": "ldesportsbar-4",
    "name": "上机时长排行榜",
    "owner": "财务组",
    "status": "优化中",
    "metric": "4 级",
    "priority": "高"
  },
  {
    "key": "ldesportsbar-5",
    "name": "赛事报名与战队管理",
    "owner": "审核组",
    "status": "可导出",
    "metric": "28 条",
    "priority": "中"
  }
];
