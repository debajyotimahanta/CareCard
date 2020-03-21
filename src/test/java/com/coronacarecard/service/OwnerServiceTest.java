package com.coronacarecard.service;

import com.coronacarecard.dao.BusinessRepository;
import com.coronacarecard.dao.UserRepository;
import com.coronacarecard.dao.entity.Business;
import com.coronacarecard.dao.entity.User;
import com.coronacarecard.exceptions.BusinessAlreadyClaimedException;
import com.coronacarecard.exceptions.BusinessNotFoundException;
import com.coronacarecard.exceptions.InternalException;
import com.coronacarecard.model.BusinessState;
import com.coronacarecard.notifications.NotificationSender;
import com.coronacarecard.notifications.NotificationType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@AutoConfigureTestDatabase
@SpringBootTest
public class OwnerServiceTest {

    @MockBean
    private NotificationSender<com.coronacarecard.model.Business> notificationSender;

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
    }

    @After
    public void cleanup() {
        businessRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test(expected = BusinessAlreadyClaimedException.class)
    public void error_if_business_already_exists_and_claimed() throws BusinessAlreadyClaimedException,
            InternalException, BusinessNotFoundException {
        ownerService.claimBusiness(existingBusinessId, "test@gmail.com", "773");

    }

    @Test
    public void existing_business_claimed_by_same_name_user_phone() throws BusinessAlreadyClaimedException,
            InternalException, BusinessNotFoundException {
        com.coronacarecard.model.Business claimedBusiness = ownerService.claimBusiness(existingBusinessId, EMAIL, PHONE);
        //TODO (arun) once width tha height is populate please uncomment
        //assertEquals(existingBusiness, claimedBusiness);
    }

    @Test(expected = BusinessAlreadyClaimedException.class)
    public void error_claim_draft_business_by_different_details_and_notify()
            throws BusinessAlreadyClaimedException, InternalException, BusinessNotFoundException {
        setState(BusinessState.Draft);
        ownerService.claimBusiness(existingBusinessId, "test@gmail.com", "773");
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

        ownerService.claimBusiness(placeId, "test@gmail.com", "773");
    }

    @Test
    public void new_business_import_and_associate_profile_and_notify()
            throws InternalException, BusinessNotFoundException, BusinessAlreadyClaimedException {
        Optional<Business> beforeClaim = businessRepository.findByExternalId(newBusinessId);
        assertFalse(beforeClaim.isPresent());
        com.coronacarecard.model.Business result = ownerService.claimBusiness(newBusinessId
                , "x" + EMAIL, PHONE);
        assertEquals(newBusinessId, result.getExternalRefId());
        Optional<Business> afterClaim = businessRepository.findByExternalId(newBusinessId);
        assertTrue(afterClaim.isPresent());
        User user = userRepository.findByEmail("x" + EMAIL);
        assertNotNull(user);
        assertEquals(PHONE, user.getPhoneNumber());
        ArgumentCaptor<com.coronacarecard.model.Business> peopleCaptor
                = ArgumentCaptor.forClass(com.coronacarecard.model.Business.class);
        verify(notificationSender).sendNotification(eq(NotificationType.BUSINESS_CLAIMED), peopleCaptor.capture());
        assertEquals(result, peopleCaptor.getValue());


    }
}