package com.amway.calculator.command;

import java.math.*;

public record AddCommand(BigDecimal operand) implements CalculatorCommand {
  public AddCommand {
    if (operand == null) throw new IllegalArgumentException("Operand is required");
  }

  public BigDecimal execute(BigDecimal current) {
    return current.add(operand, MathContext.DECIMAL128);
  }
}
