package com.coronacarecard.controller;

import com.coronacarecard.config.StripeConfiguration;
import com.coronacarecard.dao.BusinessRepository;
import com.coronacarecard.dao.entity.Business;
import com.coronacarecard.exceptions.InternalException;
import com.coronacarecard.mapper.BusinessEntityMapper;
import com.coronacarecard.service.CryptoService;
import com.coronacarecard.util.TestHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = {"AWS_ARN=arn:aws:kms:us-west-1:008731829883:key/a72c4b37-325e-4254-9a9f-38592d01e0b2",
        "spring.app.forntEndBaseUrl=http://base","spring.app.appUrl:http://appbase"})
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
public class StripePaymentControllerTest {

    @Autowired
    private StripePaymentController controller;

    @Autowired
    private BusinessRepository businessRepository;

    @MockBean
    private CryptoService cryptoService;

    @Autowired
    private BusinessEntityMapper businessEntityMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StripeConfiguration stripeConfiguration;

    private String businessId="TEST-54444";

    @Before
    public void init() throws InternalException {
        when(cryptoService.encrypt(any())).thenReturn(businessId);
        when(cryptoService.decrypt(any())).thenReturn(businessId);
    }

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
        assertEquals(String.format(expected, connectId, cryptoService.encrypt(businessEntityMapper.toModel(business).getId().toString())), onboardUrl);
    }
}
