package com.coronacarecard.service.impl;

import com.coronacarecard.dao.BusinessRepository;
import com.coronacarecard.dao.UserRepository;
import com.coronacarecard.dao.entity.User;
import com.coronacarecard.exceptions.*;
import com.coronacarecard.mapper.BusinessEntityMapper;
import com.coronacarecard.model.*;
import com.coronacarecard.notifications.NotificationSender;
import com.coronacarecard.notifications.NotificationType;
import com.coronacarecard.service.GooglePlaceService;
import com.coronacarecard.service.OwnerService;
import com.coronacarecard.service.PaymentService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class OwnerServiceImpl implements OwnerService {
    private static Log log = LogFactory.getLog(OwnerServiceImpl.class);
    @Autowired
    private BusinessRepository businessRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GooglePlaceService googlePlaceService;

    @Autowired
    private BusinessEntityMapper businessEntityMapper;

    @Autowired
    private NotificationSender<Business> notificationSender;

    @Autowired
    private NotificationSender<BusinessApprovalDetails> approvalNotificationSender;

    @Autowired
    @Qualifier("StripePaymentService")
    private PaymentService paymentService;


    @Override
    @Transactional
    public Business claimBusiness(BusinessRegistrationRequest request) throws
            BusinessAlreadyClaimedException, InternalException, BusinessNotFoundException {
        String externalId = request.getBusinessId();
        String email = request.getEmail();
        String phone = request.getPhone();
        Optional<com.coronacarecard.dao.entity.Business> businessDAO =
                businessRepository.findByExternalId(externalId);
        if (businessDAO.isPresent()
                && !BusinessState.Draft.equals(businessDAO.get().getState())) {
            if (Objects.isNull(businessDAO.get().getOwner())) {
                log.error(String.format("A business %s is %s without an owner", externalId, businessDAO.get().getState()));
                throw new InternalException("There is something wrong with this business please contact us.");
            }
            if (isSameOwner(businessDAO.get().getOwner(), email, phone)) {
                return businessEntityMapper.toModel(businessDAO.get());
            } else {
                logAndThrowBusinessClaimedException(externalId, email);
            }

        }
        if (businessDAO.isPresent()
                && !Objects.isNull(businessDAO.get().getOwner())
                && !isSameOwner(businessDAO.get().getOwner(), email, phone)) {
            logAndThrowBusinessClaimedException(externalId, email);
        }

        com.coronacarecard.dao.entity.Business business;
        if (!businessDAO.isPresent()) {
            business = businessEntityMapper.toDAO(googlePlaceService.getBusiness(externalId));
        } else {
            business = businessDAO.get();
        }


        User owner = userRepository.findByEmail(email);
        if (owner == null) {
            owner = User
                    .builder()
                    .phoneNumber(phone)
                    .email(email)
                    .build();
        }
        business.setState(BusinessState.Claimed);
        business.setDescription(request.getDescription());
        business.setOwner(owner);
        businessRepository.save(business);


        Business claimedBusiness = businessEntityMapper.toModel(business);
        notificationSender.sendNotification(NotificationType.BUSINESS_CLAIMED, claimedBusiness);
        return claimedBusiness;
    }

    private Business logAndThrowBusinessClaimedException(String externalId, String email) throws BusinessAlreadyClaimedException {
        log.info(String.format("User %s is trying to claim %s, but its already claimed",
                email, externalId));
        throw new BusinessAlreadyClaimedException();
    }

    private boolean isSameOwner(User owner, String email, String phone) {
        return owner.getEmail().equals(email)
                && owner.getPhoneNumber().equals(phone);

    }

    @Override
    public void registerOwner(String encryptedDetails, String externalRefId) {

    }

    @Override
    @Transactional
    public String approveClaim(PaymentSystem paymentSystem, UUID id) throws CustomerException {
        Optional<com.coronacarecard.dao.entity.Business> business = businessRepository.findById(id);
        if (!business.isPresent()) {
            log.error(String.format("No business with id %s exists. You cannot approve it.", id));
            throw new BusinessNotFoundException();
        }
        com.coronacarecard.dao.entity.Business businessDAO = business.get();
        if (BusinessState.Draft.equals(businessDAO.getState())) {
            log.error(String.format("Business id = %s is in Draft state, you cannot approve it", id));
            throw new BusinessClaimException("Draft business cannot be approved");
        }

        if (BusinessState.Active.equals(businessDAO)) {
            log.error(String.format("Business id = %s is in Active State so already approved and registered", id));
            throw new BusinessClaimException("Active business is already approved");
        }

        if (BusinessState.Claimed.equals(businessDAO.getState())) {
            log.info("Business is claimed now, will wait for owner to enter payment details");
            businessDAO.setState(BusinessState.Pending);
            businessDAO = businessRepository.save(businessDAO);
        }
        String url = paymentService.generateOnBoardingUrl(businessEntityMapper.toModel(businessDAO));
        approvalNotificationSender.sendNotification(
                NotificationType.BUSINESS_APPROVED,
                BusinessApprovalDetails.builder()
                        .business(businessEntityMapper.toModel(businessDAO))
                        .registrationUrl(url)
                        .build()
        );
        return url;

    }

    @Override
    @Transactional
    public void declineClaim(UUID id) throws BusinessNotFoundException {
        Optional<com.coronacarecard.dao.entity.Business> business = businessRepository.findById(id);
        if (!business.isPresent()) {
            log.error(String.format("No business with id %s exists. You cannot decline it.", id));
            throw new BusinessNotFoundException();
        }

        if (BusinessState.Draft.equals(business.get().getState())) {
            log.info("Business already marked as Draft nothing to do");
        }


        business.get().setState(BusinessState.Draft);
        business.get().setOwner(null);
        com.coronacarecard.dao.entity.Business result = businessRepository.save(business.get());
        notificationSender.sendNotification(NotificationType.BUSINESS_DECLINED,
                businessEntityMapper.toModel(result));


    }
}
