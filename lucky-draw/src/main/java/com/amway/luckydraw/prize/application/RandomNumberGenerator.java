package com.amway.luckydraw.prize.application;

@FunctionalInterface
public interface RandomNumberGenerator {
  int nextInt(int bound);
}
