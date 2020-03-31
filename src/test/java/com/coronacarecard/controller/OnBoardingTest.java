package com.coronacarecard.controller;

import com.coronacarecard.dao.BusinessRepository;
import com.coronacarecard.dao.entity.Business;
import com.coronacarecard.exceptions.BusinessNotFoundException;
import com.coronacarecard.exceptions.InternalException;
import com.coronacarecard.exceptions.PayementServiceException;
import com.coronacarecard.mapper.BusinessEntityMapper;
import com.coronacarecard.model.BusinessApprovalDetails;
import com.coronacarecard.model.BusinessState;
import com.coronacarecard.notifications.NotificationSender;
import com.coronacarecard.notifications.NotificationType;
import com.coronacarecard.service.CryptoService;
import com.coronacarecard.service.PaymentService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.IOException;
import java.util.Optional;

import static com.coronacarecard.util.TestHelper.*;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
public class OnBoardingTest {
    private static final String PLACEID  = getPlainTextPlaceId();
    private static final String EXTERNALPLACEID  = "ChIJXbraKagPkFQRR5OlYjIcCXI";
    private static final String EMAIL    = "t@t.com";
    private static final String PHONE    = "7737322612";
    private static final String AUTHCODE = "code";
    private static final String STATE    = getEncryptedPlaceId();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationSender<com.coronacarecard.model.Business> notificationSender;

    @MockBean
    private NotificationSender<BusinessApprovalDetails> approvalNotificationSender;

    @MockBean
    private CryptoService cryptoService;

    @MockBean
    private PaymentService paymentService;

    @Autowired
    private BusinessRepository businessRepository;

    @Autowired
    private BusinessEntityMapper businessEntityMapper;

    private Optional<Business> afterRegister;


    @Before
    public void init() throws InternalException, BusinessNotFoundException, PayementServiceException {
        when(paymentService.importBusiness( AUTHCODE, STATE))
                .thenAnswer(invocation -> businessEntityMapper.toModel(afterRegister.get()));

        when(cryptoService.decrypt(STATE)).thenAnswer(invocation -> afterRegister.get().getId().toString());
        when(paymentService.generateOnBoardingUrl(any())).thenReturn("onboarding_url");
    }

    /**
     * 1. business claims busiess with basic info
     * 2. Admin approves business
     * 3. Fake onboarding url which call confirm back
     * 4. Call confirm and pass state
     * 5. Confirm move the state to Active
     */
    @Test
    public void onboarding_happy_case() throws Exception, IOException {
        Optional<Business> beforeRegister = businessRepository.findByExternalId(PLACEID);
        assertFalse(beforeRegister.isPresent());
        mockMvc.perform(MockMvcRequestBuilders.post("/owner/claim")
                .content(getBusinessRegistrationRequestJson(EXTERNALPLACEID, EMAIL, PHONE))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        afterRegister = businessRepository.findByExternalId(EXTERNALPLACEID);
        assertTrue(afterRegister.isPresent());
        assertEquals(BusinessState.Claimed, afterRegister.get().getState());
        ArgumentCaptor<com.coronacarecard.model.Business> claimBusinessDetails
                = ArgumentCaptor.forClass(com.coronacarecard.model.Business.class);
        verify(notificationSender).sendNotification(eq(NotificationType.BUSINESS_CLAIMED),
                claimBusinessDetails.capture());
        assertEquals(EXTERNALPLACEID, claimBusinessDetails.getValue().getExternalRefId());
        mockMvc.perform(MockMvcRequestBuilders.get(
                "/admin/business/" + afterRegister.get().getId() + "/approve/STRIPE")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        assertEquals(BusinessState.Pending, businessRepository.findByExternalId(EXTERNALPLACEID).get().getState());
        ArgumentCaptor<BusinessApprovalDetails> approvalDetails
                = ArgumentCaptor.forClass(BusinessApprovalDetails.class);
        verify(approvalNotificationSender).sendNotification(eq(NotificationType.BUSINESS_APPROVED),
                approvalDetails.capture());
        assertEquals("onboarding_url", approvalDetails.getValue().getRegistrationUrl());
        assertEquals(EXTERNALPLACEID, claimBusinessDetails.getValue().getExternalRefId());

        mockMvc.perform(MockMvcRequestBuilders.get(
                "/payment/stripe/business/confirm?code=" + AUTHCODE + "&state=" + STATE)
                .contentType("application/json"))
                .andExpect(status().is3xxRedirection())
                .andReturn();
        assertEquals(BusinessState.Active, businessRepository.findByExternalId(EXTERNALPLACEID).get().getState());


    }
}
