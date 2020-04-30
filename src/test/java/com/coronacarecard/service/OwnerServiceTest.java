package com.coronacarecard.service;

import com.coronacarecard.dao.BusinessRepository;
import com.coronacarecard.dao.UserRepository;
import com.coronacarecard.dao.entity.Business;
import com.coronacarecard.dao.entity.User;
import com.coronacarecard.exceptions.*;
import com.coronacarecard.model.BusinessApprovalDetails;
import com.coronacarecard.model.BusinessRegistrationRequest;
import com.coronacarecard.model.BusinessState;
import com.coronacarecard.model.PaymentSystem;
import com.coronacarecard.notifications.NotificationSender;
import com.coronacarecard.notifications.NotificationType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@AutoConfigureTestDatabase
@SpringBootTest
@EnableAutoConfiguration(exclude={LiquibaseAutoConfiguration.class})
public class OwnerServiceTest {

    public static final String DESC = "Hello world";

    @MockBean
    private NotificationSender<com.coronacarecard.model.Business> notificationSender;

    @MockBean
    private NotificationSender<BusinessApprovalDetails> approvalNotificationSender;

    @MockBean
    private CloudStorageService cloudStorageService;

    @Autowired
    private OwnerService ownerService;

    @Autowired
    private BusinessService businessService;

    @Autowired
    private BusinessRepository businessRepository;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private PaymentService paymentService;

    private String existingBusinessId = "ChIJicMwN4lskFQR9brCQh07Xyo";
    private String newBusinessId = "ChIJKV8LiAcPkFQRgaK8WZdjnuY";
    private Business existingBusinessDAO;
    private static String EMAIL = "e@e.com";
    private static String PHONE = "1234567890";
    private com.coronacarecard.model.Business existingBusiness;
    private User user;

    @Before
    public void init() throws BusinessNotFoundException, InternalException {
        MockitoAnnotations.initMocks(this);
        existingBusiness = businessService.createOrUpdate(existingBusinessId);
        existingBusinessDAO = businessRepository.findByExternalId(existingBusinessId).get();
        user = User.builder()
                .email(EMAIL)
                .phoneNumber(PHONE)
                .build();
        userRepository.save(user);
        existingBusinessDAO = existingBusinessDAO.toBuilder()
                .state(BusinessState.Active)
                .owner(user)
                .build();
        businessRepository.save(existingBusinessDAO);

        when(paymentService.generateOnBoardingUrl(any())).thenReturn("url");
    }

    @After
    public void cleanup() {
        businessRepository.deleteAll();
        userRepository.deleteAll();
    }

    private BusinessRegistrationRequest getReq(String businessId, String email, String phone) {
        return BusinessRegistrationRequest.builder()
                .phone(phone)
                .email(email)
                .businessId(businessId)
                .description(DESC)
                .build();
    }

    @Test(expected = BusinessAlreadyClaimedException.class)
    public void error_if_business_already_exists_and_claimed() throws BusinessAlreadyClaimedException,
            InternalException, BusinessNotFoundException {
        ownerService.claimBusiness(getReq(existingBusinessId, "test@gmail.com", "773"));

    }


    @Test
    public void existing_business_claimed_by_same_name_user_phone() throws BusinessAlreadyClaimedException,
            InternalException, BusinessNotFoundException {
        com.coronacarecard.model.Business claimedBusiness = ownerService
                .claimBusiness(getReq(existingBusinessId, EMAIL, PHONE));
        //TODO (arun) once width tha height is populate please uncomment
        //assertEquals(existingBusiness, claimedBusiness);
    }

    @Test(expected = BusinessAlreadyClaimedException.class)
    public void error_claim_draft_business_by_different_details_and_notify()
            throws BusinessAlreadyClaimedException, InternalException, BusinessNotFoundException {
        setState(BusinessState.Draft);
        ownerService.claimBusiness(getReq(existingBusinessId, "test@gmail.com", "773"));
    }

    private void setState(BusinessState state) {
        existingBusinessDAO = existingBusinessDAO.toBuilder()
                .state(state)
                .build();
        businessRepository.save(existingBusinessDAO);
    }

    @Test(expected = InternalException.class)
    public void error_when_active_without_owner() throws BusinessNotFoundException,
            InternalException, BusinessAlreadyClaimedException {
        String placeId = "ChIJXbraKagPkFQRR5OlYjIcCXI";
        businessService.createOrUpdate(placeId);
        existingBusinessDAO = businessRepository.findByExternalId(placeId).get();

        existingBusinessDAO = existingBusinessDAO.toBuilder()
                .state(BusinessState.Active)
                .build();
        businessRepository.save(existingBusinessDAO);

        ownerService.claimBusiness(getReq(placeId, "test@gmail.com", "773"));
    }

    @Test
    public void new_business_import_and_associate_profile_and_notify()
            throws InternalException, BusinessNotFoundException, BusinessAlreadyClaimedException {
        Optional<Business> beforeClaim = businessRepository.findByExternalId(newBusinessId);
        assertFalse(beforeClaim.isPresent());
        com.coronacarecard.model.Business result = ownerService.claimBusiness(getReq(newBusinessId
                , "x" + EMAIL, PHONE));
        assertEquals(newBusinessId, result.getExternalRefId());
        Optional<Business> afterClaim = businessRepository.findByExternalId(newBusinessId);
        assertTrue(afterClaim.isPresent());
        assertEquals(BusinessState.Claimed, afterClaim.get().getState());
        User user = userRepository.findByEmail("x" + EMAIL);
        assertNotNull(user);
        assertEquals(PHONE, user.getPhoneNumber());
        assertEquals(DESC, afterClaim.get().getDescription());
        ArgumentCaptor<com.coronacarecard.model.Business> businessArgumentCaptor
                = ArgumentCaptor.forClass(com.coronacarecard.model.Business.class);
        verify(notificationSender).sendNotification(eq(NotificationType.BUSINESS_CLAIMED),
                businessArgumentCaptor.capture());
        assertEquals(result, businessArgumentCaptor.getValue());

    }

    @Test
    public void same_owner_claims_two_business() throws InternalException,
            BusinessNotFoundException, BusinessAlreadyClaimedException {

        ownerService.claimBusiness(getReq(newBusinessId, EMAIL, PHONE));
        User owner = userRepository.findByEmail(EMAIL);
        assertEquals(2, owner.getBusiness().size());
    }

    @Test
    public void approve_owner_claim() throws InternalException, CustomerException {
        com.coronacarecard.model.Business createdBusines = ownerService.claimBusiness(getReq(newBusinessId, EMAIL, PHONE));
        String onboardURL = ownerService.approveClaim(PaymentSystem.STRIPE, createdBusines.getId());
        assertEquals("url", onboardURL);
        ArgumentCaptor<BusinessApprovalDetails> approvalDetails
                = ArgumentCaptor.forClass(BusinessApprovalDetails.class);
        verify(approvalNotificationSender).sendNotification(eq(NotificationType.BUSINESS_APPROVED),
                approvalDetails.capture());
        assertEquals(onboardURL, approvalDetails.getValue().getRegistrationUrl());
        createdBusines.setStatus(BusinessState.Pending);
        assertEquals(createdBusines, approvalDetails.getValue().getBusiness());
        Business storedBusiness = businessRepository.findById(createdBusines.getId()).get();
        assertEquals(BusinessState.Pending, storedBusiness.getState());
    }

    @Test
    public void approve_pending() throws CustomerException, InternalException {
        com.coronacarecard.model.Business createdBusines =
                ownerService.claimBusiness(getReq(newBusinessId, EMAIL, PHONE));
        String onboardURL = ownerService.approveClaim(PaymentSystem.STRIPE, createdBusines.getId());
        Business storedBusiness = businessRepository.findById(createdBusines.getId()).get();
        assertEquals(BusinessState.Pending, storedBusiness.getState());
        com.coronacarecard.model.Business businessRetry
                = ownerService.claimBusiness(getReq(newBusinessId, EMAIL, PHONE));
        assertEquals(createdBusines.getId(), createdBusines.getId());

    }

    @Test (expected = BusinessClaimException.class)
    public void error_approve_draft_business() throws CustomerException {
        setState(BusinessState.Draft);
        ownerService.approveClaim(PaymentSystem.STRIPE, existingBusiness.getId());

    }

    @Test
    public void decline_claimned_business()
            throws InternalException, BusinessNotFoundException, BusinessAlreadyClaimedException {
        com.coronacarecard.model.Business business =
                ownerService.claimBusiness(getReq(newBusinessId, EMAIL, PHONE));
        Business createdBusiness = businessRepository.findByExternalId(newBusinessId).get();
        assertEquals(BusinessState.Claimed, createdBusiness.getState());
        assertEquals(EMAIL, createdBusiness.getOwner().getEmail());
        ownerService.declineClaim(createdBusiness.getId());
        Business afterDecline = businessRepository.findByExternalId(newBusinessId).get();
        assertEquals(BusinessState.Draft, afterDecline.getState());
        assertNull(afterDecline.getOwner());
        ArgumentCaptor<com.coronacarecard.model.Business> businessArgumentCaptor
                = ArgumentCaptor.forClass(com.coronacarecard.model.Business.class);
        verify(notificationSender).sendNotification(eq(NotificationType.BUSINESS_DECLINED),
                businessArgumentCaptor.capture());
        assertEquals(newBusinessId, afterDecline.getExternalRefId());
    }

    @Test
    public void claim_draft_busuiness() throws BusinessNotFoundException, InternalException,
            BusinessAlreadyClaimedException {
        businessService.getOrCreate(newBusinessId);
        ownerService.claimBusiness(getReq(newBusinessId, EMAIL, PHONE));
        Business createdBusiness = businessRepository.findByExternalId(newBusinessId).get();
        assertEquals(BusinessState.Claimed, createdBusiness.getState());
        assertEquals(EMAIL, createdBusiness.getOwner().getEmail());

    }

    @Test
    public void claim_noexistent_business() throws InternalException, BusinessNotFoundException, BusinessAlreadyClaimedException {
        ownerService.claimBusiness(BusinessRegistrationRequest.builder()
                .businessId(newBusinessId)
                .description("test")
                .email("test@gmail.com")
                .phone("911")
                .build());
    }
}