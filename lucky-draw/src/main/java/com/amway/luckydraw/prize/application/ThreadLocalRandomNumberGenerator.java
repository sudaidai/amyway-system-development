package com.amway.luckydraw.prize.application;

import java.util.concurrent.ThreadLocalRandom;
import org.springframework.stereotype.Component;

@Component
public class ThreadLocalRandomNumberGenerator implements RandomNumberGenerator {
  public int nextInt(int bound) {
    return ThreadLocalRandom.current().nextInt(bound);
  }
}
