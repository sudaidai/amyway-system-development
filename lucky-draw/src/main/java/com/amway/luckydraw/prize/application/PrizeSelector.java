package com.amway.luckydraw.prize.application;

import com.amway.luckydraw.prize.domain.Prize;
import java.util.List;

public interface PrizeSelector {
  PrizeSelection select(List<Prize> prizes);
}
