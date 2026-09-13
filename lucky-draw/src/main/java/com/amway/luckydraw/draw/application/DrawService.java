package com.amway.luckydraw.draw.application;

import com.amway.luckydraw.draw.api.DrawDtos.DrawResult;

public interface DrawService {
  DrawResult drawOnce(String userId, long campaignId, String requestId, int index);
}
