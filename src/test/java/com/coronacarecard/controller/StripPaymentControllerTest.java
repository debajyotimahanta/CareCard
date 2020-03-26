package com.coronacarecard.controller;

import com.coronacarecard.dao.BusinessRepository;
import com.coronacarecard.dao.entity.Business;
import com.coronacarecard.mapper.BusinessEntityMapper;
import com.coronacarecard.service.BusinessService;
import com.coronacarecard.service.CryptoService;
import com.coronacarecard.util.TestHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(properties="spring.app.forntEndBaseUrl=http://base")
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
public class StripPaymentControllerTest {

    @Autowired
    private StripPaymentController controller;

    @Autowired
    private BusinessRepository businessRepository;

    @Autowired
    private CryptoService cryptoService;

    @Autowired
    private BusinessEntityMapper businessEntityMapper;

    @Autowired
    private MockMvc mockMvc;

   @Test
    public void generate_onbaording_url_for_existing_business() throws Exception{
       Business business= TestHelper.createEntry(businessRepository,"123-456-789","TEST-54444","test");

       String connectId="JJJJ";
       String expected="https://connect.stripe.com/oauth/authorize?client_id=%1$s&state=%2$s&scope=read_write&response_type=code";
       MvcResult response= mockMvc.perform(
               MockMvcRequestBuilders
                       .get("/payment/strip/business/onboard/"+business.getId().toString())
                       .contentType("application/json"))
               .andExpect(status().isOk())
               .andReturn();
       String onboardUrl=response.getResponse().getContentAsString();
       assertEquals(String.format(expected,connectId,cryptoService.encryptBusiness(businessEntityMapper.toModel(business))),onboardUrl);
   }
}
