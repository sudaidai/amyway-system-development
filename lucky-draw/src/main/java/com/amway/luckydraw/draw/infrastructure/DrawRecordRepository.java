package com.amway.luckydraw.draw.infrastructure;

import com.amway.luckydraw.draw.domain.DrawRecord;
import jakarta.persistence.LockModeType;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DrawRecordRepository extends JpaRepository<DrawRecord, Long> {
  List<DrawRecord> findByUserIdAndCampaignIdAndRequestIdOrderByDrawIndex(
      String userId, Long campaignId, String requestId);

  Optional<DrawRecord> findByUserIdAndCampaignIdAndRequestIdAndDrawIndex(
      String userId, Long campaignId, String requestId, int drawIndex);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query(
      """
      select d from DrawRecord d
       where d.userId = :userId
         and d.campaignId = :campaignId
         and d.requestId = :requestId
         and d.drawIndex = :drawIndex
      """)
  Optional<DrawRecord> findIdempotentResultForUpdate(
      @Param("userId") String userId,
      @Param("campaignId") Long campaignId,
      @Param("requestId") String requestId,
      @Param("drawIndex") int drawIndex);
}
