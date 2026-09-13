package com.amway.luckydraw.campaign.api;

import com.amway.luckydraw.campaign.api.CampaignDtos.*;
import com.amway.luckydraw.campaign.application.CampaignService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/v1")
@SecurityRequirement(name = "bearerAuth")
public class CampaignController {
  private final CampaignService service;

  public CampaignController(CampaignService service) {
    this.service = service;
  }

  @GetMapping("/campaigns/{campaignId}")
  @Operation(summary = "Get a campaign and current prize inventory")
  public CampaignView get(@PathVariable @Positive long campaignId) {
    return service.get(campaignId);
  }

  @PostMapping("/admin/campaigns")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(
      summary = "Create a complete campaign configuration",
      description = "Requires ROLE_ADMIN")
  public CampaignView create(@Valid @RequestBody CreateCampaignRequest r) {
    return service.create(r);
  }

  @PostMapping("/admin/campaigns/{campaignId}/prizes")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(
      summary = "Add a prize while atomically rebalancing no-prize probability",
      description = "Requires ROLE_ADMIN")
  public CampaignView addPrize(
      @PathVariable long campaignId, @Valid @RequestBody ConfigurePrizeRequest r) {
    return service.addPrize(campaignId, r);
  }

  @PutMapping("/admin/campaigns/{campaignId}/prizes/{prizeId}")
  @Operation(
      summary = "Update a prize and validate the final distribution",
      description = "Requires ROLE_ADMIN")
  public CampaignView updatePrize(
      @PathVariable long campaignId,
      @PathVariable long prizeId,
      @Valid @RequestBody ConfigurePrizeRequest r) {
    return service.updatePrize(campaignId, prizeId, r);
  }
}
