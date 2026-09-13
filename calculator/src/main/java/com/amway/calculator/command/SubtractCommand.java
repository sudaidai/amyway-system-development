package com.amway.calculator.command;

import java.math.*;

public record SubtractCommand(BigDecimal operand) implements CalculatorCommand {
  public SubtractCommand {
    if (operand == null) throw new IllegalArgumentException("Operand is required");
  }

  public BigDecimal execute(BigDecimal current) {
    return current.subtract(operand, MathContext.DECIMAL128);
  }
}
