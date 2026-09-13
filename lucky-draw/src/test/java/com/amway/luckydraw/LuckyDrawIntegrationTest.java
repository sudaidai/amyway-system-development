package com.amway.luckydraw;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.amway.luckydraw.campaign.api.CampaignDtos.*;
import com.amway.luckydraw.campaign.application.CampaignService;
import com.amway.luckydraw.campaign.domain.CampaignStatus;
import com.amway.luckydraw.campaign.infrastructure.CampaignRepository;
import com.amway.luckydraw.common.*;
import com.amway.luckydraw.draw.api.DrawDtos.*;
import com.amway.luckydraw.draw.application.*;
import com.amway.luckydraw.draw.domain.DrawOutcome;
import com.amway.luckydraw.draw.infrastructure.*;
import com.amway.luckydraw.prize.application.*;
import com.amway.luckydraw.prize.domain.Prize;
import com.amway.luckydraw.prize.infrastructure.PrizeRepository;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class LuckyDrawIntegrationTest extends MySqlContainerSupport {
  @Autowired CampaignService campaignService;
  @Autowired SingleDrawExecutor draws;
  @Autowired MultiDrawService multiDraw;
  @Autowired CampaignRepository campaigns;
  @Autowired PrizeRepository prizes;
  @Autowired UserCampaignDrawRepository counters;
  @Autowired DrawRecordRepository records;
  @MockBean PrizeSelector selector;

  @BeforeEach
  void clean() {
    records.deleteAll();
    counters.deleteAll();
    prizes.deleteAll();
    campaigns.deleteAll();
  }

  @Test
  @Transactional
  void repositoriesAndAtomicInventoryUpdate() {
    CampaignView c = create(1, 3);
    Long p = c.prizes().getFirst().id();
    assertThat(prizes.findByCampaignIdOrderById(c.id())).hasSize(1);
    long initialVersion = prizes.findById(p).orElseThrow().getVersion();
    assertThat(prizes.decrementInventory(p)).isOne();
    Prize afterSuccessfulDecrement = prizes.findById(p).orElseThrow();
    assertThat(afterSuccessfulDecrement.getRemainingQuantity()).isZero();
    assertThat(afterSuccessfulDecrement.getVersion()).isEqualTo(initialVersion + 1);
    assertThat(prizes.decrementInventory(p)).isZero();
    Prize afterRejectedDecrement = prizes.findById(p).orElseThrow();
    assertThat(afterRejectedDecrement.getRemainingQuantity()).isZero();
    assertThat(afterRejectedDecrement.getVersion()).isEqualTo(initialVersion + 1);
  }

  @Test
  void winningAndNoPrizeResultsPersistAndIncrementCount() {
    CampaignView c = create(1, 2);
    Prize p = prizes.findById(c.prizes().getFirst().id()).orElseThrow();
    when(selector.select(anyList())).thenReturn(PrizeSelection.win(p), PrizeSelection.noPrize());
    assertThat(draws.drawOnce("u", c.id(), "r", 0).result()).isEqualTo(DrawOutcome.WIN);
    assertThat(draws.drawOnce("u", c.id(), "r", 1).result()).isEqualTo(DrawOutcome.NO_PRIZE);
    assertThat(records.count()).isEqualTo(2);
    assertThat(counters.findByUserIdAndCampaignId("u", c.id()).orElseThrow().getDrawCount())
        .isEqualTo(2);
  }

  @Test
  void exhaustedPrizeRetriesThreeTimesThenReturnsNoPrize() {
    CampaignView c = create(0, 1);
    Prize p = prizes.findById(c.prizes().getFirst().id()).orElseThrow();
    when(selector.select(anyList())).thenReturn(PrizeSelection.win(p));
    assertThat(draws.drawOnce("u", c.id(), "r", 0).result()).isEqualTo(DrawOutcome.NO_PRIZE);
    verify(selector, times(3)).select(anyList());
  }

  @Test
  void multiDrawCommitsIndependentlyStopsAtLimitAndRetryIsIdempotent() {
    CampaignView c = create(5, 2);
    when(selector.select(anyList())).thenReturn(PrizeSelection.noPrize());
    DrawResponse first = multiDraw.draw("u", c.id(), new MultiDrawRequest("batch", 3));
    assertThat(first.results()).hasSize(2);
    assertThat(first.stoppedReason()).isEqualTo(ErrorCode.DRAW_LIMIT_EXCEEDED);
    DrawResponse retry = multiDraw.draw("u", c.id(), new MultiDrawRequest("batch", 2));
    assertThat(retry.results()).hasSize(2);
    assertThat(counters.findByUserIdAndCampaignId("u", c.id()).orElseThrow().getDrawCount())
        .isEqualTo(2);
  }

  @Test
  void transactionRollsBackCounterWhenSelectionFails() {
    CampaignView c = create(1, 1);
    when(selector.select(anyList())).thenThrow(new IllegalStateException("forced"));
    assertThatThrownBy(() -> draws.drawOnce("rollback-user", c.id(), "rollback", 0))
        .isInstanceOf(IllegalStateException.class);
    assertThat(
            counters
                .findByUserIdAndCampaignId("rollback-user", c.id())
                .orElseThrow()
                .getDrawCount())
        .isZero();
    assertThat(
            records.findByUserIdAndCampaignIdAndRequestIdOrderByDrawIndex(
                "rollback-user", c.id(), "rollback"))
        .isEmpty();
  }

  @Test
  void concurrentDuplicateRequestReturnsOnePersistedResultAndConsumesOneDraw() {
    CampaignView c = create(5, 3);
    when(selector.select(anyList())).thenReturn(PrizeSelection.noPrize());
    ExecutorService pool = Executors.newFixedThreadPool(2);
    try {
      Callable<DrawResult> duplicate = () -> draws.drawOnce("duplicate-user", c.id(), "same", 0);
      List<Future<DrawResult>> futures = pool.invokeAll(List.of(duplicate, duplicate));
      assertThat(futures.get(0).get()).isEqualTo(futures.get(1).get());
      assertThat(
              records.findByUserIdAndCampaignIdAndRequestIdOrderByDrawIndex(
                  "duplicate-user", c.id(), "same"))
          .hasSize(1);
      assertThat(
              counters
                  .findByUserIdAndCampaignId("duplicate-user", c.id())
                  .orElseThrow()
                  .getDrawCount())
          .isOne();
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      pool.shutdownNow();
    }
  }

  @Test
  void concurrentInventoryAndDrawLimitNeverOversell() {
    CampaignView c = create(1, 3);
    Prize p = prizes.findById(c.prizes().getFirst().id()).orElseThrow();
    when(selector.select(anyList())).thenReturn(PrizeSelection.win(p));
    ExecutorService pool = Executors.newFixedThreadPool(10);
    try {
      List<Callable<Object>> jobs = new ArrayList<>();
      for (int i = 0; i < 20; i++) {
        int n = i;
        jobs.add(
            () -> {
              try {
                return draws.drawOnce("same-user", c.id(), "r" + n, 0);
              } catch (DrawLimitExceededException e) {
                return e;
              }
            });
      }
      List<Future<Object>> futures = pool.invokeAll(jobs);
      long wins = 0, success = 0;
      for (Future<Object> f : futures) {
        Object x = f.get();
        if (!(x instanceof Exception)) {
          success++;
          if (((com.amway.luckydraw.draw.api.DrawDtos.DrawResult) x).result() == DrawOutcome.WIN)
            wins++;
        }
      }
      assertThat(success).isLessThanOrEqualTo(3);
      assertThat(wins).isLessThanOrEqualTo(1);
      assertThat(prizes.findById(p.getId()).orElseThrow().getRemainingQuantity())
          .isGreaterThanOrEqualTo(0);
      assertThat(
              counters.findByUserIdAndCampaignId("same-user", c.id()).orElseThrow().getDrawCount())
          .isLessThanOrEqualTo(3);
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      pool.shutdownNow();
    }
  }

  private CampaignView create(int stock, int max) {
    return campaignService.create(
        new CreateCampaignRequest(
            "campaign",
            max,
            CampaignStatus.ACTIVE,
            BigDecimal.ZERO,
            null,
            null,
            List.of(new PrizeInput("Prize", stock, new BigDecimal("100.00")))));
  }
}
