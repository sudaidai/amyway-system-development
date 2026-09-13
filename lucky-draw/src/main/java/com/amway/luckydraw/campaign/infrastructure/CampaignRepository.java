package com.amway.luckydraw.campaign.infrastructure;

import com.amway.luckydraw.campaign.domain.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CampaignRepository extends JpaRepository<Campaign, Long> {}
