package com.generated.ldesportsbar.config;

import java.math.BigDecimal;

public final class AppConstants {
  public static final String APP_NAME = "电竞馆上机管理系统";
  public static final String APP_CODE = "ldesportsbar";

  /** 余额计费：每小时 6 元。 */
  public static final BigDecimal HOURLY_RATE = new BigDecimal("6.00");
  public static final BigDecimal MINUTE_RATE = HOURLY_RATE.divide(new BigDecimal("60"));

  /** 10 小时时长包：售价 48 元，购买后 30 天有效。 */
  public static final int PACKAGE_MINUTES = 600;
  public static final BigDecimal PACKAGE_PRICE = new BigDecimal("48.00");
  public static final int PACKAGE_VALID_DAYS = 30;

  public static final String SEAT_IDLE = "IDLE";
  public static final String SEAT_IN_USE = "IN_USE";
  public static final String SEAT_BROKEN = "BROKEN";

  public static final String SESSION_ACTIVE = "ACTIVE";
  public static final String SESSION_STOPPED = "STOPPED";

  private AppConstants() {
  }
}
