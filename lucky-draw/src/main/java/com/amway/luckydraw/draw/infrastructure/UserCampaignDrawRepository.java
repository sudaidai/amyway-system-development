package com.amway.luckydraw.draw.infrastructure;

import com.amway.luckydraw.draw.domain.UserCampaignDraw;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface UserCampaignDrawRepository extends JpaRepository<UserCampaignDraw, Long> {
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select d from UserCampaignDraw d where d.userId=:userId and d.campaignId=:campaignId")
  Optional<UserCampaignDraw> findForUpdate(
      @Param("userId") String userId, @Param("campaignId") Long campaignId);

  Optional<UserCampaignDraw> findByUserIdAndCampaignId(String userId, Long campaignId);

  @Modifying
  @Query(
      value =
          "insert ignore into user_campaign_draw(user_id,campaign_id,draw_count,version) values (:userId,:campaignId,0,0)",
      nativeQuery = true)
  int initializeIfMissing(@Param("userId") String userId, @Param("campaignId") Long campaignId);
}
