package com.amway.luckydraw.draw.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(
    name = "draw_records",
    uniqueConstraints =
        @UniqueConstraint(
            name = "uk_draw_request_index",
            columnNames = {"user_id", "campaign_id", "request_id", "draw_index"}),
    indexes = @Index(name = "idx_draw_user_campaign", columnList = "user_id,campaign_id"))
public class DrawRecord {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "user_id", nullable = false)
  private String userId;

  @Column(name = "campaign_id", nullable = false)
  private Long campaignId;

  @Column(name = "request_id", nullable = false, length = 80)
  private String requestId;

  @Column(name = "draw_index", nullable = false)
  private int drawIndex;

  private Long prizeId;
  private String prizeName;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private DrawOutcome result;

  @Column(nullable = false, updatable = false)
  private Instant createdAt = Instant.now();

  protected DrawRecord() {}

  public DrawRecord(
      String userId,
      Long campaignId,
      String requestId,
      int drawIndex,
      Long prizeId,
      String prizeName,
      DrawOutcome result) {
    this.userId = userId;
    this.campaignId = campaignId;
    this.requestId = requestId;
    this.drawIndex = drawIndex;
    this.prizeId = prizeId;
    this.prizeName = prizeName;
    this.result = result;
  }

  public Long getId() {
    return id;
  }

  public int getDrawIndex() {
    return drawIndex;
  }

  public Long getCampaignId() {
    return campaignId;
  }

  public Long getPrizeId() {
    return prizeId;
  }

  public String getPrizeName() {
    return prizeName;
  }

  public DrawOutcome getResult() {
    return result;
  }
}
