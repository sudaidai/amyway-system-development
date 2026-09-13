package com.amway.calculator.command;

import java.math.*;

public record MultiplyCommand(BigDecimal operand) implements CalculatorCommand {
  public MultiplyCommand {
    if (operand == null) throw new IllegalArgumentException("Operand is required");
  }

  public BigDecimal execute(BigDecimal current) {
    return current.multiply(operand, MathContext.DECIMAL128);
  }
}
