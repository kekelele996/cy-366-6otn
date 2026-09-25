package com.generated.ldesportsbar.model;

import java.util.List;

public record OverviewResponse(
  String appName,
  String appCode,
  String description,
  List<FeatureItem> features,
  List<KpiItem> kpis,
  List<OperationRecord> records
) {
}
