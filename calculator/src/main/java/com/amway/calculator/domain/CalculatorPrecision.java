package com.amway.calculator.domain;

import java.math.RoundingMode;

public final class CalculatorPrecision {

  public static final int SCALE = 6;
  public static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

  private CalculatorPrecision() {}
}
