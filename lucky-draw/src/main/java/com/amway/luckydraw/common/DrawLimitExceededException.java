package com.amway.luckydraw.common;

public class DrawLimitExceededException extends BusinessException {
  public DrawLimitExceededException() {
    super(ErrorCode.DRAW_LIMIT_EXCEEDED, "Maximum draw limit has been reached");
  }
}
