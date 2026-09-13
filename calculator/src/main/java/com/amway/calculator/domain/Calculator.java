package com.amway.calculator.domain;

import java.math.BigDecimal;

public interface Calculator {
  BigDecimal add(BigDecimal operand);

  BigDecimal subtract(BigDecimal operand);

  BigDecimal multiply(BigDecimal operand);

  BigDecimal divide(BigDecimal operand);

  BigDecimal clear();

  BigDecimal undo();

  BigDecimal redo();

  BigDecimal getResult();

  String display();
}
