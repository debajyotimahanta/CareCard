package com.coronacarecard.service;

import com.coronacarecard.model.Business;
import com.coronacarecard.model.PaymentSystem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(properties="spring.app.forntEndBaseUrl=http://base")
public class PaymentServiceTest {

    @Autowired
    private CryptoService cryptoService;

    @Autowired
    private PaymentService paymentService;

    @Test
    public void generate_onboarding_url(){
        String connectId="JJJJ";
        String expected="https://connect.stripe.com/oauth/authorize?client_id=%1$s&state=%2$s&scope=read_write&response_type=code";

        Business business=Business.builder().id(10L).externalRefId("ext-100").build();
        String onboardingUrl=paymentService.generateOnBoardingUrl(PaymentSystem.STRIPE,business);

        assertEquals(String.format(expected,connectId,cryptoService.encryptBusiness(business)),onboardingUrl);

    }
}
