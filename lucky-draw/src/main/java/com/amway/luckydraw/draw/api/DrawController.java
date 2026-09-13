package com.amway.luckydraw.draw.api;

import com.amway.luckydraw.draw.api.DrawDtos.*;
import com.amway.luckydraw.draw.application.MultiDrawService;
import com.amway.luckydraw.security.AuthenticatedPrincipal;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/v1/campaigns")
@SecurityRequirement(name = "bearerAuth")
public class DrawController {
  private final MultiDrawService service;

  public DrawController(MultiDrawService service) {
    this.service = service;
  }

  @PostMapping("/{campaignId}/draws")
  @Operation(
      summary = "Draw once or multiple times",
      description =
          "Uses the JWT subject as draw owner. Each draw commits independently; processing stops with DRAW_LIMIT_EXCEEDED when quota is reached. requestId makes retries idempotent.")
  public DrawResponse draw(
      @PathVariable @Positive long campaignId,
      @Valid @RequestBody MultiDrawRequest request,
      @Parameter(hidden = true) Authentication authentication) {
    AuthenticatedPrincipal principal = (AuthenticatedPrincipal) authentication.getPrincipal();
    return service.draw(principal.userId(), campaignId, request);
  }
}
