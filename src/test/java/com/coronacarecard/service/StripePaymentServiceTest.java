package com.coronacarecard.service;

import com.coronacarecard.config.StripeConfiguration;
import com.coronacarecard.dao.BusinessRepository;
import com.coronacarecard.exceptions.BusinessAlreadyClaimedException;
import com.coronacarecard.exceptions.BusinessNotFoundException;
import com.coronacarecard.exceptions.InternalException;
import com.coronacarecard.exceptions.PayementServiceException;
import com.coronacarecard.model.Business;
import com.coronacarecard.model.BusinessRegistrationRequest;
import com.coronacarecard.service.payment.StripeCalls;
import com.google.gson.JsonObject;
import com.stripe.exception.StripeException;
import com.stripe.model.oauth.TokenResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestDatabase
public class StripePaymentServiceTest {

    private static final String AUTH_CODE = "auth";
    private static final String STRIPE_USER_ID = "StripeId";
    private static final String REFRESH_TOKEN = "refresh_token";
    private static final String ACCESS_TOEKN = "access_token";

    @Autowired
    private PaymentService paymentService;

    private String business_id = "ChIJKV8LiAcPkFQRgaK8WZdjnuY";

    @Autowired
    private StripeConfiguration stripeConfiguration;

    @Autowired
    @Qualifier("StripePaymentService")
    private PaymentService stripePayementService;

    @MockBean
    private StripeCalls stripeCalls;

    @Autowired
    private OwnerService ownerService;

    @Autowired
    private BusinessRepository businessRepository;

    @Autowired
    private CryptoService cryptoService;

    @Test
    public void generate_onboarding_url() {
        String connectId = stripeConfiguration.getClientId();
        String expected = stripeConfiguration.getConnectUrl();

        Business business = Business.builder().id(UUID.randomUUID()).externalRefId(business_id).build();
        String onboardingUrl = paymentService.generateOnBoardingUrl(business);
        System.out.println(onboardingUrl);
        assertEquals(String.format(expected, connectId, (business.getId().toString())), onboardingUrl);

    }

    @Test
    public void importBusinessTest() throws InternalException, PayementServiceException,
            BusinessNotFoundException, BusinessAlreadyClaimedException, StripeException {
        Business calimedBusiness = ownerService.claimBusiness(BusinessRegistrationRequest.builder()
                .businessId(business_id)
                .description("test")
                .email("test@gmail.com")
                .phone("911")
                .build());
        TokenResponse response = mock(TokenResponse.class);
        when(response.getStripeUserId()).thenReturn(STRIPE_USER_ID);
        JsonObject json = new JsonObject();
        json.addProperty("refresh_token", REFRESH_TOKEN);
        json.addProperty("access_token", ACCESS_TOEKN);
        when(response.getRawJsonObject()).thenReturn(json);
        when(stripeCalls.token(AUTH_CODE)).thenReturn(response);
        stripePayementService.importBusiness(AUTH_CODE, calimedBusiness.getId().toString());
        com.coronacarecard.dao.entity.Business businessWithAccount =
                businessRepository.findByExternalId(business_id).get();
        assertEquals(STRIPE_USER_ID, businessWithAccount.getOwner().getAccount().getExternalRefId());
        ;
        assertEquals(ACCESS_TOEKN,
                cryptoService.decrypt(businessWithAccount.getOwner().getAccount().getAccessToken()));
        assertEquals(REFRESH_TOKEN,
                cryptoService.decrypt(businessWithAccount.getOwner().getAccount().getRefreshToken()));


    }

}
