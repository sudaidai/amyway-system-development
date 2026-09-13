package com.amway.luckydraw.draw.application;

import com.amway.luckydraw.draw.api.DrawDtos.DrawResult;
import org.springframework.stereotype.Service;

@Service
public class SingleDrawExecutor implements DrawService {
  private final DrawCounterInitializer counterInitializer;
  private final SingleDrawTransaction transaction;

  public SingleDrawExecutor(
      DrawCounterInitializer counterInitializer, SingleDrawTransaction transaction) {
    this.counterInitializer = counterInitializer;
    this.transaction = transaction;
  }

  @Override
  public DrawResult drawOnce(String userId, long campaignId, String requestId, int index) {
    counterInitializer.initialize(userId, campaignId);
    return transaction.execute(userId, campaignId, requestId, index);
  }
}
