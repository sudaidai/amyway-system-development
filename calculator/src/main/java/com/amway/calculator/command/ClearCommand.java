package com.amway.calculator.command;

import java.math.BigDecimal;

public final class ClearCommand implements CalculatorCommand {
  public BigDecimal execute(BigDecimal current) {
    return BigDecimal.ZERO;
  }
}
