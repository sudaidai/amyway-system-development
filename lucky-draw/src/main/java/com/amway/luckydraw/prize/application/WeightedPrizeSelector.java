package com.amway.luckydraw.prize.application;

import com.amway.luckydraw.prize.domain.Prize;
import java.math.RoundingMode;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class WeightedPrizeSelector implements PrizeSelector {
  public static final int BUCKETS = 10_000;
  private final RandomNumberGenerator random;

  public WeightedPrizeSelector(RandomNumberGenerator random) {
    this.random = random;
  }

  public PrizeSelection select(List<Prize> prizes) {
    int point = random.nextInt(BUCKETS), cursor = 0;
    for (Prize prize : prizes) {
      cursor +=
          prize
              .getProbability()
              .movePointRight(2)
              .setScale(0, RoundingMode.UNNECESSARY)
              .intValueExact();
      if (point < cursor) return PrizeSelection.win(prize);
    }
    return PrizeSelection.noPrize();
  }
}
