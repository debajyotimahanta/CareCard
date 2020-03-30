package com.coronacarecard.service;

import com.coronacarecard.config.StripeConfiguration;
import com.coronacarecard.exceptions.InternalException;
import com.coronacarecard.model.Business;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StripePaymentServiceTest {

    @MockBean
    private CryptoService cryptoService;

    @Autowired
    private PaymentService paymentService;

    private String business_id="TEST_54444";

    @Autowired
    private StripeConfiguration stripeConfiguration;

    @Before
    public void init() throws InternalException {
        when(cryptoService.encrypt(any())).thenReturn(business_id);
        when(cryptoService.decrypt(any())).thenReturn(business_id);
    }

    @Test
    public void generate_onboarding_url(){
        String connectId=stripeConfiguration.getClientId();
        String expected=stripeConfiguration.getConnectUrl();

        Business business=Business.builder().id(10L).externalRefId(business_id).build();
        String onboardingUrl=paymentService.generateOnBoardingUrl(business);

        System.out.println(onboardingUrl);
        assertEquals(String.format(expected,connectId,cryptoService.encrypt(business.getId().toString())),onboardingUrl);

    }

}
