package com.amway.luckydraw.campaign.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "lucky_draw_campaigns")
public class Campaign {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(name = "max_draw_per_user", nullable = false)
  private int maxDrawPerUser;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private CampaignStatus status;

  @Column(name = "no_prize_probability", nullable = false, precision = 7, scale = 4)
  private BigDecimal noPrizeProbability;

  private Instant startTime;
  private Instant endTime;

  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  @Column(nullable = false)
  private Instant updatedAt;

  protected Campaign() {}

  public Campaign(
      String name,
      int maxDrawPerUser,
      CampaignStatus status,
      BigDecimal noPrizeProbability,
      Instant startTime,
      Instant endTime) {
    update(name, maxDrawPerUser, status, noPrizeProbability, startTime, endTime);
  }

  public void update(
      String name, int max, CampaignStatus status, BigDecimal noPrize, Instant start, Instant end) {
    this.name = name;
    this.maxDrawPerUser = max;
    this.status = status;
    this.noPrizeProbability = noPrize;
    this.startTime = start;
    this.endTime = end;
  }

  public boolean isAvailableAt(Instant now) {
    return status == CampaignStatus.ACTIVE
        && (startTime == null || !now.isBefore(startTime))
        && (endTime == null || now.isBefore(endTime));
  }

  @PrePersist
  void create() {
    createdAt = updatedAt = Instant.now();
  }

  @PreUpdate
  void updateTimestamp() {
    updatedAt = Instant.now();
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public int getMaxDrawPerUser() {
    return maxDrawPerUser;
  }

  public CampaignStatus getStatus() {
    return status;
  }

  public BigDecimal getNoPrizeProbability() {
    return noPrizeProbability;
  }

  public Instant getStartTime() {
    return startTime;
  }

  public Instant getEndTime() {
    return endTime;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }
}
