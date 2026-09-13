package com.amway.calculator.command;

import java.math.BigDecimal;

@FunctionalInterface
public interface CalculatorCommand {
  BigDecimal execute(BigDecimal currentValue);
}
