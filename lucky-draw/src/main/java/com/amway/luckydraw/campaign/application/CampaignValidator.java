package com.amway.luckydraw.campaign.application;

import com.amway.luckydraw.campaign.domain.Campaign;
import com.amway.luckydraw.common.*;
import com.amway.luckydraw.prize.domain.Prize;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class CampaignValidator {
  private static final BigDecimal HUNDRED = new BigDecimal("100");

  public void available(Campaign c) {
    if (!c.isAvailableAt(Instant.now()))
      throw new BusinessException(
          ErrorCode.CAMPAIGN_NOT_AVAILABLE, "Campaign is not active at this time");
  }

  public void configuration(List<Prize> prizes, BigDecimal noPrize) {
    if (!validProbability(noPrize)) invalid();
    BigDecimal sum = noPrize;
    for (Prize p : prizes) {
      if (p.getTotalQuantity() < 0
          || p.getRemainingQuantity() < 0
          || p.getRemainingQuantity() > p.getTotalQuantity()
          || !validProbability(p.getProbability())) invalid();
      sum = sum.add(p.getProbability());
    }
    if (sum.compareTo(HUNDRED) != 0) invalid();
  }

  public void schedule(Instant startTime, Instant endTime) {
    if (startTime != null && endTime != null && !endTime.isAfter(startTime))
      throw new BusinessException(
          ErrorCode.INVALID_CAMPAIGN_CONFIGURATION,
          "Campaign end time must be after its start time");
  }

  private static boolean validProbability(BigDecimal value) {
    return value != null
        && value.signum() >= 0
        && Math.max(0, value.stripTrailingZeros().scale()) <= 2;
  }

  private void invalid() {
    throw new BusinessException(
        ErrorCode.INVALID_PROBABILITY_CONFIGURATION,
        "Prize and no-prize probabilities must be non-negative, support 0.01% resolution, and total 100%");
  }
}
