package com.amway.luckydraw.prize.application;

import com.amway.luckydraw.prize.domain.Prize;
import java.util.Optional;

public record PrizeSelection(Optional<Prize> prize) {
  public static PrizeSelection noPrize() {
    return new PrizeSelection(Optional.empty());
  }

  public static PrizeSelection win(Prize p) {
    return new PrizeSelection(Optional.of(p));
  }
}
