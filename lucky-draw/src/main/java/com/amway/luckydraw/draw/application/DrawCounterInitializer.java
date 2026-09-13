package com.amway.luckydraw.draw.application;

import com.amway.luckydraw.draw.infrastructure.UserCampaignDrawRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DrawCounterInitializer {
  private final UserCampaignDrawRepository counters;

  public DrawCounterInitializer(UserCampaignDrawRepository counters) {
    this.counters = counters;
  }

  /** Commits creation before the caller attempts to lock the row. */
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void initialize(String userId, long campaignId) {
    counters.initializeIfMissing(userId, campaignId);
  }
}
