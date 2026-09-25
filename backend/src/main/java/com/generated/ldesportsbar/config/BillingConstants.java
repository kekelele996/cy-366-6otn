package com.generated.ldesportsbar.config;

import java.math.BigDecimal;

public final class BillingConstants {
  /** 余额计费：每小时 6 元 */
  public static final BigDecimal HOURLY_RATE = new BigDecimal("6.00");
  /** 折算下来每分钟 0.10 元 */
  public static final BigDecimal MINUTE_RATE = new BigDecimal("0.10");
  /** 时长包规格：10 小时 */
  public static final int PACKAGE_HOURS = 10;
  public static final int PACKAGE_MINUTES = PACKAGE_HOURS * 60;
  /** 10 小时包售价 50 元，自购买起 30 天有效 */
  public static final BigDecimal PACKAGE_PRICE = new BigDecimal("50.00");
  public static final int PACKAGE_VALID_DAYS = 30;

  public static final String PACKAGE_ACTIVE = "ACTIVE";
  public static final String PACKAGE_DEPLETED = "DEPLETED";
  public static final String PACKAGE_EXPIRED = "EXPIRED";

  public static final String MACHINE_IDLE = "IDLE";
  public static final String MACHINE_IN_USE = "IN_USE";

  public static final String SESSION_ACTIVE = "ACTIVE";
  public static final String SESSION_CLOSED = "CLOSED";

  public static final String SOURCE_PACKAGE = "PACKAGE";
  public static final String SOURCE_BALANCE = "BALANCE";

  public static final String FLOW_RECHARGE = "RECHARGE";
  public static final String FLOW_PACKAGE_PURCHASE = "PACKAGE_PURCHASE";
  public static final String FLOW_PACKAGE_DEDUCT = "PACKAGE_DEDUCT";
  public static final String FLOW_BALANCE_DEDUCT = "BALANCE_DEDUCT";
  public static final String FLOW_PACKAGE_REFUND = "PACKAGE_REFUND";
  public static final String FLOW_BALANCE_REFUND = "BALANCE_REFUND";

  private BillingConstants() {
  }
}
