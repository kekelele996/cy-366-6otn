package com.generated.ldesportsbar.exception;

/** 业务冲突：重复注册、机位占用、余额不足、重复操作等。映射为 HTTP 409。 */
public class ConflictException extends RuntimeException {
  public ConflictException(String message) {
    super(message);
  }
}
