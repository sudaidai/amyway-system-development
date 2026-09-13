package com.amway.luckydraw;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.amway.luckydraw.campaign.api.CampaignDtos.*;
import com.amway.luckydraw.campaign.application.CampaignService;
import com.amway.luckydraw.campaign.domain.CampaignStatus;
import com.amway.luckydraw.security.JwtService;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class LuckyDrawApiTest extends MySqlContainerSupport {
  @Autowired MockMvc mvc;
  @Autowired JwtService jwt;
  @Autowired CampaignService campaigns;

  @Test
  void unauthenticatedIs401AndUserAdminRequestIs403() throws Exception {
    mvc.perform(get("/api/v1/campaigns/1")).andExpect(status().isUnauthorized());
    mvc.perform(
            post("/api/v1/admin/campaigns")
                .header("Authorization", "Bearer " + jwt.generateToken("u", "ROLE_USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
        .andExpect(status().isForbidden());
  }

  @Test
  void authenticatedDrawTraversesFullStack() throws Exception {
    CampaignView c =
        campaigns.create(
            new CreateCampaignRequest(
                "api",
                1,
                CampaignStatus.ACTIVE,
                BigDecimal.ZERO,
                null,
                null,
                List.of(new PrizeInput("P", 1, new BigDecimal("100.00")))));
    mvc.perform(
            post("/api/v1/campaigns/" + c.id() + "/draws")
                .header("Authorization", "Bearer " + jwt.generateToken("user-1", "ROLE_USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"requestId\":\"api-request\",\"count\":1}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.results.length()").value(1))
        .andExpect(jsonPath("$.results[0].result").value("WIN"));
  }
}
