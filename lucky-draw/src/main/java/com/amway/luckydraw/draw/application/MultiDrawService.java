package com.amway.luckydraw.draw.application;

import com.amway.luckydraw.common.*;
import com.amway.luckydraw.draw.api.DrawDtos.*;
import java.util.*;
import org.springframework.stereotype.Service;

@Service
public class MultiDrawService {
  private final DrawService draws;

  public MultiDrawService(DrawService draws) {
    this.draws = draws;
  }

  public DrawResponse draw(String user, long campaign, MultiDrawRequest request) {
    List<DrawResult> results = new ArrayList<>();
    ErrorCode stopped = null;
    for (int i = 0; i < request.count(); i++) {
      try {
        results.add(draws.drawOnce(user, campaign, request.requestId(), i));
      } catch (DrawLimitExceededException e) {
        stopped = e.getCode();
        break;
      }
    }
    return new DrawResponse(request.requestId(), results, stopped);
  }
}
