package com.amway.luckydraw.prize.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "prizes", indexes = @Index(name = "idx_prize_campaign", columnList = "campaign_id"))
public class Prize {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "campaign_id", nullable = false)
  private Long campaignId;

  @Column(nullable = false)
  private String name;

  @Column(name = "total_quantity", nullable = false)
  private int totalQuantity;

  @Column(name = "remaining_quantity", nullable = false)
  private int remainingQuantity;

  @Column(nullable = false, precision = 7, scale = 4)
  private BigDecimal probability;

  @Version private long version;

  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  @Column(nullable = false)
  private Instant updatedAt;

  protected Prize() {}

  public Prize(Long campaignId, String name, int quantity, BigDecimal probability) {
    this.campaignId = campaignId;
    this.name = name;
    this.totalQuantity = quantity;
    this.remainingQuantity = quantity;
    this.probability = probability;
  }

  public void update(
      String name, int totalQuantity, int remainingQuantity, BigDecimal probability) {
    this.name = name;
    this.totalQuantity = totalQuantity;
    this.remainingQuantity = remainingQuantity;
    this.probability = probability;
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

  public Long getCampaignId() {
    return campaignId;
  }

  public String getName() {
    return name;
  }

  public int getTotalQuantity() {
    return totalQuantity;
  }

  public int getRemainingQuantity() {
    return remainingQuantity;
  }

  public BigDecimal getProbability() {
    return probability;
  }

  public long getVersion() {
    return version;
  }
}
