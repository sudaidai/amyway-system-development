package com.amway.luckydraw.campaign.api;

import com.amway.luckydraw.campaign.domain.CampaignStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public final class CampaignDtos {
  private CampaignDtos() {}

  public record PrizeInput(
      @NotBlank String name,
      @PositiveOrZero int quantity,
      @NotNull @DecimalMin("0.00") @Digits(integer = 3, fraction = 2) BigDecimal probability) {}

  public record CreateCampaignRequest(
      @NotBlank String name,
      @Positive int maxDrawPerUser,
      @NotNull CampaignStatus status,
      @NotNull @DecimalMin("0.00") @Digits(integer = 3, fraction = 2) BigDecimal noPrizeProbability,
      Instant startTime,
      Instant endTime,
      @NotEmpty List<@Valid PrizeInput> prizes) {}

  public record ConfigurePrizeRequest(
      @NotBlank String name,
      @PositiveOrZero int totalQuantity,
      @PositiveOrZero int remainingQuantity,
      @NotNull @DecimalMin("0.00") @Digits(integer = 3, fraction = 2) BigDecimal probability,
      @NotNull @DecimalMin("0.00") @Digits(integer = 3, fraction = 2)
          BigDecimal noPrizeProbability) {}

  public record PrizeView(
      Long id, String name, int totalQuantity, int remainingQuantity, BigDecimal probability) {}

  public record CampaignView(
      Long id,
      String name,
      int maxDrawPerUser,
      CampaignStatus status,
      BigDecimal noPrizeProbability,
      Instant startTime,
      Instant endTime,
      List<PrizeView> prizes) {}
}
