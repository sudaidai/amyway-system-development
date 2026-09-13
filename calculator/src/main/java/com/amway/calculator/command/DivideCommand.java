package com.amway.calculator.command;

import com.amway.calculator.exception.DivisionByZeroException;
import java.math.*;

public record DivideCommand(BigDecimal operand) implements CalculatorCommand {
  public DivideCommand {
    if (operand == null) throw new IllegalArgumentException("Operand is required");
    if (operand.signum() == 0) throw new DivisionByZeroException();
  }

  public BigDecimal execute(BigDecimal current) {
    return current.divide(operand, MathContext.DECIMAL128);
  }
}
