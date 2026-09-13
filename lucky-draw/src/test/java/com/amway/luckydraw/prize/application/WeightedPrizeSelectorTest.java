package com.amway.luckydraw.prize.application;

import static org.assertj.core.api.Assertions.*;

import com.amway.luckydraw.prize.domain.Prize;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class WeightedPrizeSelectorTest {
  @Test
  void exactBoundariesSelectExpectedPrize() {
    List<Prize> p = List.of(prize("A", "10.00"), prize("B", "20.00"));
    assertThat(select(p, 999).prize()).get().extracting(Prize::getName).isEqualTo("A");
    assertThat(select(p, 1000).prize()).get().extracting(Prize::getName).isEqualTo("B");
    assertThat(select(p, 2999).prize()).get().extracting(Prize::getName).isEqualTo("B");
    assertThat(select(p, 3000).prize()).isEmpty();
  }

  @Test
  void supportsPointZeroOnePercentResolution() {
    Prize p = prize("rare", "0.01");
    assertThat(select(List.of(p), 0).prize()).isPresent();
    assertThat(select(List.of(p), 1).prize()).isEmpty();
  }

  private static PrizeSelection select(List<Prize> p, int n) {
    return new WeightedPrizeSelector(new FixedRandomNumberGenerator(n)).select(p);
  }

  private static Prize prize(String n, String probability) {
    return new Prize(1L, n, 1, new BigDecimal(probability));
  }
}
