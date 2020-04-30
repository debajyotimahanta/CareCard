package com.coronacarecard.controller;

import com.coronacarecard.config.StripeConfiguration;
import com.coronacarecard.dao.BusinessRepository;
import com.coronacarecard.dao.entity.Business;
import com.coronacarecard.mapper.BusinessEntityMapper;
import com.coronacarecard.util.TestHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
@EnableAutoConfiguration(exclude={LiquibaseAutoConfiguration.class})
public class StripePaymentControllerTest {

    //TODO (deba) add test
    @Test
    public void confirm() {
    }

    @Autowired
    private StripePaymentController controller;

    @Autowired
    private BusinessRepository businessRepository;

    @Autowired
    private BusinessEntityMapper businessEntityMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StripeConfiguration stripeConfiguration;

    private String businessId="TEST-54444";

    @Test
    public void generate_onbaording_url_for_existing_business() throws Exception {
        Business business = TestHelper.createEntry(businessRepository, "123-456-789", "TEST-54444", "test");

        String connectId = stripeConfiguration.getClientId();
        String expected  = stripeConfiguration.getConnectUrl();
        MvcResult response = mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/payment/stripe/business/onboard/" + business.getId().toString())
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        String onboardUrl = response.getResponse().getContentAsString();
        assertEquals(String.format(expected, connectId, businessEntityMapper.toModel(business).getId().toString(), "http://localhost:5000"), onboardUrl);
    }

    @Test
    public void validate_confirm_code_is_not_nullOrEmpty() throws Exception {
        MvcResult result = mockMvc.perform(get("/payment/stripe/business/confirm")
        .contentType("application/json")
        .param("code", "")
        .param("state", "somerandomvalue"))
                  .andExpect(status().isBadRequest())
                .andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("must not be empty"));
    }

    @Test
    public void validate_confirm_state_is_not_nullOrEmpty() throws Exception {
        MvcResult result = mockMvc.perform(get("/payment/stripe/business/confirm")
                .contentType("application/json")
                .param("code", "somerandomvalue")
                .param("state", ""))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("must not be empty"));
    }
}
