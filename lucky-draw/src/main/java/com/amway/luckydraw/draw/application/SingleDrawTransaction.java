package com.amway.luckydraw.draw.application;

import com.amway.luckydraw.campaign.application.CampaignValidator;
import com.amway.luckydraw.campaign.domain.Campaign;
import com.amway.luckydraw.campaign.infrastructure.CampaignRepository;
import com.amway.luckydraw.common.BusinessException;
import com.amway.luckydraw.common.ErrorCode;
import com.amway.luckydraw.draw.api.DrawDtos.DrawResult;
import com.amway.luckydraw.draw.domain.DrawOutcome;
import com.amway.luckydraw.draw.domain.DrawRecord;
import com.amway.luckydraw.draw.domain.UserCampaignDraw;
import com.amway.luckydraw.draw.infrastructure.DrawRecordRepository;
import com.amway.luckydraw.draw.infrastructure.UserCampaignDrawRepository;
import com.amway.luckydraw.prize.application.PrizeSelection;
import com.amway.luckydraw.prize.application.PrizeSelector;
import com.amway.luckydraw.prize.domain.Prize;
import com.amway.luckydraw.prize.infrastructure.PrizeRepository;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SingleDrawTransaction {
  static final int MAX_DRAW_RETRY = 3;
  private static final Logger log = LoggerFactory.getLogger(SingleDrawTransaction.class);
  private final CampaignRepository campaigns;
  private final PrizeRepository prizes;
  private final UserCampaignDrawRepository counters;
  private final DrawRecordRepository records;
  private final CampaignValidator validator;
  private final PrizeSelector selector;

  public SingleDrawTransaction(
      CampaignRepository campaigns,
      PrizeRepository prizes,
      UserCampaignDrawRepository counters,
      DrawRecordRepository records,
      CampaignValidator validator,
      PrizeSelector selector) {
    this.campaigns = campaigns;
    this.prizes = prizes;
    this.counters = counters;
    this.records = records;
    this.validator = validator;
    this.selector = selector;
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public DrawResult execute(String userId, long campaignId, String requestId, int index) {
    UserCampaignDraw counter = counters.findForUpdate(userId, campaignId).orElseThrow();
    Optional<DrawRecord> existing =
        records.findIdempotentResultForUpdate(userId, campaignId, requestId, index);
    if (existing.isPresent()) return result(existing.get());

    Campaign campaign =
        campaigns
            .findById(campaignId)
            .orElseThrow(
                () -> new BusinessException(ErrorCode.CAMPAIGN_NOT_FOUND, "Campaign not found"));
    validator.available(campaign);
    counter.consume(campaign.getMaxDrawPerUser());
    List<Prize> configured = prizes.findByCampaignIdOrderById(campaignId);
    validator.configuration(configured, campaign.getNoPrizeProbability());

    DrawRecord record = null;
    for (int attempt = 0; attempt < MAX_DRAW_RETRY; attempt++) {
      PrizeSelection selected = selector.select(configured);
      if (selected.prize().isEmpty()) {
        record = noPrize(userId, campaignId, requestId, index);
        break;
      }
      Prize prize = selected.prize().get();
      if (prizes.decrementInventory(prize.getId()) == 1) {
        record =
            new DrawRecord(
                userId,
                campaignId,
                requestId,
                index,
                prize.getId(),
                prize.getName(),
                DrawOutcome.WIN);
        break;
      }
    }
    if (record == null) record = noPrize(userId, campaignId, requestId, index);
    record = records.save(record);
    log.info(
        "draw completed drawId={} campaignId={} userId={} result={} prizeId={}",
        record.getId(),
        campaignId,
        userId,
        record.getResult(),
        record.getPrizeId());
    return result(record);
  }

  private static DrawRecord noPrize(String userId, long campaignId, String requestId, int index) {
    return new DrawRecord(userId, campaignId, requestId, index, null, null, DrawOutcome.NO_PRIZE);
  }

  private static DrawResult result(DrawRecord record) {
    return new DrawResult(
        record.getDrawIndex(), record.getResult(), record.getPrizeId(), record.getPrizeName());
  }
}
