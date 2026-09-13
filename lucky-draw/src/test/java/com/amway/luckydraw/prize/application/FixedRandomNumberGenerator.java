package com.amway.luckydraw.prize.application;

/** Deterministic random source used by prize-selection and draw-service tests. */
public final class FixedRandomNumberGenerator implements RandomNumberGenerator {
  private final int value;

  public FixedRandomNumberGenerator(int value) {
    this.value = value;
  }

  @Override
  public int nextInt(int bound) {
    if (value < 0 || value >= bound) {
      throw new IllegalArgumentException("Fixed value must be within the requested bound");
    }
    return value;
  }
}
