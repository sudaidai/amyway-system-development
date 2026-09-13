package com.amway.luckydraw.draw.api;

import com.amway.luckydraw.common.ErrorCode;
import com.amway.luckydraw.draw.domain.DrawOutcome;
import jakarta.validation.constraints.*;
import java.util.List;

public final class DrawDtos {
  private DrawDtos() {}

  public record MultiDrawRequest(
      @NotBlank @Size(max = 80) String requestId, @Min(1) @Max(100) int count) {}

  public record DrawResult(int index, DrawOutcome result, Long prizeId, String prizeName) {}

  public record DrawResponse(String requestId, List<DrawResult> results, ErrorCode stoppedReason) {}
}
