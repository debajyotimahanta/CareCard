package com.coronacarecard.service.payment;

import com.coronacarecard.config.StripeConfiguration;
import com.coronacarecard.dao.BusinessAccountDetailRepository;
import com.coronacarecard.dao.BusinessRepository;
import com.coronacarecard.dao.entity.Business;
import com.coronacarecard.dao.entity.BusinessAccountDetail;
import com.coronacarecard.dao.entity.User;
import com.coronacarecard.exceptions.BusinessNotFoundException;
import com.coronacarecard.exceptions.InternalException;
import com.coronacarecard.exceptions.PayementServiceException;
import com.coronacarecard.service.CryptoService;
import com.coronacarecard.service.PaymentService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@AutoConfigureTestDatabase
@SpringBootTest
// We cannot test this without going throught onboariding process. But if we manually run the first test
// Then we onboard and pass the token it works
@Ignore
public class StripePaymentServiceImplTest {

    private static final UUID                  BUSINESS_ID = UUID.randomUUID();
    private static final BusinessAccountDetail ACCOUNT     = BusinessAccountDetail.builder()
            .build();
    private static       String                STATE       = "encrypt_business_id";
    @Autowired
    @Qualifier("StripePaymentService")
    private              PaymentService        paymentService;

    @Autowired
    private StripeConfiguration stripeConfiguration;

    @MockBean
    private BusinessRepository                businessRepository;
    @MockBean
    private CryptoService                     cryptoService;
    @MockBean
    private BusinessAccountDetailRepository   businessAccountDetailRepository;
    private Business                          fakeBusinessDAO = Business.builder().id(BUSINESS_ID)
            .owner(User.builder().account(ACCOUNT).build())
            .build();
    private com.coronacarecard.model.Business fakeBusiness    =
            com.coronacarecard.model.Business.builder().id(BUSINESS_ID).build();

    @Before
    public void init() throws InternalException {
        when(businessRepository.findById(BUSINESS_ID)).thenReturn(Optional.ofNullable(fakeBusinessDAO));
        when(cryptoService.encrypt(String.valueOf(BUSINESS_ID))).thenReturn(STATE);
        when(cryptoService.decrypt(STATE)).thenReturn(String.valueOf(BUSINESS_ID));
        when(businessAccountDetailRepository.findBusiness(BUSINESS_ID)).thenReturn(fakeBusinessDAO);
    }

    @Test
    public void testGenerateUrl() {
        String url = paymentService.generateOnBoardingUrl(fakeBusiness)
                + "&redirect_uri=https://connect.stripe.com/connect/default/oauth/test";
        assertEquals("", url);
    }

    @Test
    public void importBusiness() throws InternalException, PayementServiceException, BusinessNotFoundException {
        String                            authCode = "ac_H0csVTWLWupJQ3P8wOiw2uWZQk3T70XQ";
        com.coronacarecard.model.Business result   = paymentService.importBusiness(authCode, STATE);
        assertEquals(BUSINESS_ID, result.getId());
    }
}