package com.amway.luckydraw.prize.infrastructure;

import com.amway.luckydraw.prize.domain.Prize;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface PrizeRepository extends JpaRepository<Prize, Long> {
  List<Prize> findByCampaignIdOrderById(Long campaignId);

  @Modifying(flushAutomatically = true, clearAutomatically = true)
  @Query(
      """
      UPDATE Prize p
         SET p.remainingQuantity = p.remainingQuantity - 1,
             p.version = p.version + 1
       WHERE p.id = :id
         AND p.remainingQuantity > 0
      """)
  int decrementInventory(@Param("id") Long id);
}
