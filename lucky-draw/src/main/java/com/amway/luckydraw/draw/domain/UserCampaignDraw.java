package com.amway.luckydraw.draw.domain;

import jakarta.persistence.*;

@Entity
@Table(
    name = "user_campaign_draw",
    uniqueConstraints =
        @UniqueConstraint(
            name = "uk_user_campaign",
            columnNames = {"user_id", "campaign_id"}),
    indexes = @Index(name = "idx_user_campaign", columnList = "user_id,campaign_id"))
public class UserCampaignDraw {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "user_id", nullable = false)
  private String userId;

  @Column(name = "campaign_id", nullable = false)
  private Long campaignId;

  @Column(name = "draw_count", nullable = false)
  private int drawCount;

  @Version private long version;

  protected UserCampaignDraw() {}

  public UserCampaignDraw(String userId, Long campaignId) {
    this.userId = userId;
    this.campaignId = campaignId;
  }

  public void consume(int limit) {
    if (drawCount >= limit) throw new com.amway.luckydraw.common.DrawLimitExceededException();
    drawCount++;
  }

  public String getUserId() {
    return userId;
  }

  public Long getCampaignId() {
    return campaignId;
  }

  public int getDrawCount() {
    return drawCount;
  }
}
