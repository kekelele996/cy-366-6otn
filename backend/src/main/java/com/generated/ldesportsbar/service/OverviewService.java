package com.generated.ldesportsbar.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.generated.ldesportsbar.model.FeatureItem;
import com.generated.ldesportsbar.model.KpiItem;
import com.generated.ldesportsbar.model.OperationRecord;
import com.generated.ldesportsbar.model.OverviewResponse;

@Service
public class OverviewService {
  public OverviewResponse getOverview() {
    return new OverviewResponse(
      "电竞馆上机管理系统",
      "ldesportsbar",
      "面向电竞馆/网咖门店，提供机位状态监控、会员充值消费和赛事管理的运营工具。",
      List.of(new FeatureItem(1, "机位/包厢实时状态看板", "以网格或列表形式展示所有机位/包厢的实时状态（空闲/使用中/故障/预约），支持按区域筛选，点击可查看详情。", "已上线", "88%"),
        new FeatureItem(2, "会员充值与时长包", "会员账户支持充值余额，购买时长包（如10小时/30小时/月卡），消费时优先扣除时长包余额，不足时扣余额。", "排期中", "31 单"),
        new FeatureItem(3, "机位预约与续费", "会员可提前预约指定机位和时段，到店扫码开机，使用过程中可续费延长上机时间，快到期前提醒续费。", "巡检中", "10 项"),
        new FeatureItem(4, "上机时长排行榜", "记录会员累计上机时长，生成日/周/月排行榜，激励高频玩家，支持按游戏类型分类统计。", "优化中", "4 级"),
        new FeatureItem(5, "赛事报名与战队管理", "门店发布电竞赛事（如LOL/CSGO/王者荣耀），玩家以个人或战队形式报名，系统自动抽签分组，记录比赛结果和战绩。", "可导出", "28 条")),
      List.of(new KpiItem("今日处理", "106", "+12%", "primary"),
        new KpiItem("预约/订单", "43", "+8%", "warm"),
        new KpiItem("履约率", "91%", "+3%", "cool"),
        new KpiItem("待处理", "10", "需跟进", "neutral")),
      List.of(new OperationRecord("ldesportsbar-1", "机位/包厢实时状态看板", "运营组", "已上线", "88%", "高"),
        new OperationRecord("ldesportsbar-2", "会员充值与时长包", "管理员", "排期中", "31 单", "中"),
        new OperationRecord("ldesportsbar-3", "机位预约与续费", "服务台", "巡检中", "10 项", "低"),
        new OperationRecord("ldesportsbar-4", "上机时长排行榜", "财务组", "优化中", "4 级", "高"),
        new OperationRecord("ldesportsbar-5", "赛事报名与战队管理", "审核组", "可导出", "28 条", "中"))
    );
  }
}
