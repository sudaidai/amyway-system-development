package com.amway.luckydraw.campaign.application;

import static org.assertj.core.api.Assertions.*;

import com.amway.luckydraw.common.BusinessException;
import com.amway.luckydraw.prize.domain.Prize;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class CampaignValidatorTest {
  private final CampaignValidator validator = new CampaignValidator();

  @Test
  void acceptsEquivalentHundredRepresentationsAndRemainder() {
    assertThatCode(() -> validator.configuration(List.of(prize("70.0")), new BigDecimal("30.00")))
        .doesNotThrowAnyException();
    assertThatCode(() -> validator.configuration(List.of(prize("100.00")), BigDecimal.ZERO))
        .doesNotThrowAnyException();
  }

  @Test
  void rejectsNinetyNineOneHundredOneNegativeAndExcessResolution() {
    for (String p : new String[] {"99", "101", "-1", "99.999"})
      assertThatThrownBy(() -> validator.configuration(List.of(prize(p)), BigDecimal.ZERO))
          .isInstanceOf(BusinessException.class);
  }

  private static Prize prize(String probability) {
    return new Prize(1L, "P", 1, new BigDecimal(probability));
  }
}
