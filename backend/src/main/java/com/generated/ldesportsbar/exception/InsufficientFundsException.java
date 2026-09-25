package com.generated.ldesportsbar.exception;

import java.math.BigDecimal;

/** 余额不足以支付剩余时段。映射为 HTTP 409，前端提示充值，机位与账户保持不变。 */
public class InsufficientFundsException extends ConflictException {
  public InsufficientFundsException(BigDecimal required, BigDecimal balance) {
    super(String.format("余额不足：剩余时段需 %.2f 元，账户余额 %.2f 元，请充值后再开机，机位已保留",
      required, balance));
  }
}
