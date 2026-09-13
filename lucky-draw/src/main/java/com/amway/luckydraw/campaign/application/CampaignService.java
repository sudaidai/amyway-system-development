package com.amway.luckydraw.campaign.application;

import com.amway.luckydraw.campaign.api.CampaignDtos.*;
import com.amway.luckydraw.campaign.domain.Campaign;
import com.amway.luckydraw.campaign.infrastructure.CampaignRepository;
import com.amway.luckydraw.common.*;
import com.amway.luckydraw.prize.domain.Prize;
import com.amway.luckydraw.prize.infrastructure.PrizeRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CampaignService {
  private final CampaignRepository campaigns;
  private final PrizeRepository prizes;
  private final CampaignValidator validator;

  public CampaignService(
      CampaignRepository campaigns, PrizeRepository prizes, CampaignValidator validator) {
    this.campaigns = campaigns;
    this.prizes = prizes;
    this.validator = validator;
  }

  @Transactional
  public CampaignView create(CreateCampaignRequest r) {
    validator.schedule(r.startTime(), r.endTime());
    Campaign c =
        campaigns.save(
            new Campaign(
                r.name(),
                r.maxDrawPerUser(),
                r.status(),
                r.noPrizeProbability(),
                r.startTime(),
                r.endTime()));
    List<Prize> items =
        r.prizes().stream()
            .map(p -> new Prize(c.getId(), p.name(), p.quantity(), p.probability()))
            .toList();
    validator.configuration(items, r.noPrizeProbability());
    return view(c, prizes.saveAll(items));
  }

  @Transactional(readOnly = true)
  public CampaignView get(long id) {
    Campaign c = find(id);
    return view(c, prizes.findByCampaignIdOrderById(id));
  }

  @Transactional
  public CampaignView addPrize(long campaignId, ConfigurePrizeRequest r) {
    Campaign c = find(campaignId);
    List<Prize> items = new java.util.ArrayList<>(prizes.findByCampaignIdOrderById(campaignId));
    Prize p = new Prize(campaignId, r.name(), r.totalQuantity(), r.probability());
    items.add(p);
    validator.configuration(items, r.noPrizeProbability());
    c.update(
        c.getName(),
        c.getMaxDrawPerUser(),
        c.getStatus(),
        r.noPrizeProbability(),
        c.getStartTime(),
        c.getEndTime());
    prizes.save(p);
    return view(c, items);
  }

  @Transactional
  public CampaignView updatePrize(long campaignId, long prizeId, ConfigurePrizeRequest r) {
    Campaign c = find(campaignId);
    List<Prize> items = new java.util.ArrayList<>(prizes.findByCampaignIdOrderById(campaignId));
    Prize p =
        items.stream()
            .filter(x -> x.getId().equals(prizeId))
            .findFirst()
            .orElseThrow(() -> new BusinessException(ErrorCode.PRIZE_NOT_FOUND, "Prize not found"));
    p.update(r.name(), r.totalQuantity(), r.remainingQuantity(), r.probability());
    validator.configuration(items, r.noPrizeProbability());
    c.update(
        c.getName(),
        c.getMaxDrawPerUser(),
        c.getStatus(),
        r.noPrizeProbability(),
        c.getStartTime(),
        c.getEndTime());
    return view(c, items);
  }

  private Campaign find(long id) {
    return campaigns
        .findById(id)
        .orElseThrow(
            () -> new BusinessException(ErrorCode.CAMPAIGN_NOT_FOUND, "Campaign not found"));
  }

  private static CampaignView view(Campaign c, List<Prize> ps) {
    return new CampaignView(
        c.getId(),
        c.getName(),
        c.getMaxDrawPerUser(),
        c.getStatus(),
        c.getNoPrizeProbability(),
        c.getStartTime(),
        c.getEndTime(),
        ps.stream()
            .map(
                p ->
                    new PrizeView(
                        p.getId(),
                        p.getName(),
                        p.getTotalQuantity(),
                        p.getRemainingQuantity(),
                        p.getProbability()))
            .toList());
  }
}
