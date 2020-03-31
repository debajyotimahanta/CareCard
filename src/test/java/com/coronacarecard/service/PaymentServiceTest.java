package com.coronacarecard.service;

import com.coronacarecard.model.Business;
import com.coronacarecard.model.PaymentSystem;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = {"AWS_ARN=arn:aws:kms:us-west-1:008731829883:key/a72c4b37-325e-4254-9a9f-38592d01e0b2",
        "spring.app.forntEndBaseUrl=http://base"})
public class PaymentServiceTest {

    @Autowired
    private CryptoService cryptoService;

    @Autowired
    private PaymentService paymentService;

    @Ignore
    @Test
    public void generate_onboarding_url(){
        // FIXME Encrypt method provides a different key based encryption everytime invoked. Cannot call the same method twice and compare the cipher text.
        String connectId="JJJJ";
        String expected="https://connect.stripe.com/oauth/authorize?client_id=%1$s&state=%2$s&scope=read_write&response_type=code";

        Business business=Business.builder().id(UUID.randomUUID()).externalRefId("ext-100").build();
        String onboardingUrl=paymentService.generateOnBoardingUrl(PaymentSystem.STRIPE,business);

        assertEquals(String.format(expected,connectId,cryptoService.encrypt(business.getId().toString())),onboardingUrl);

    }
}
