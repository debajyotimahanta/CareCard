package com.coronacarecard.service.impl;

import com.coronacarecard.dao.BusinessRepository;
import com.coronacarecard.dao.UserRepository;
import com.coronacarecard.dao.entity.User;
import com.coronacarecard.exceptions.BusinessAlreadyClaimedException;
import com.coronacarecard.exceptions.BusinessNotFoundException;
import com.coronacarecard.exceptions.InternalException;
import com.coronacarecard.mapper.BusinessEntityMapper;
import com.coronacarecard.model.Business;
import com.coronacarecard.model.BusinessRegistrationRequest;
import com.coronacarecard.model.BusinessState;
import com.coronacarecard.notifications.NotificationSender;
import com.coronacarecard.notifications.NotificationType;
import com.coronacarecard.service.GooglePlaceService;
import com.coronacarecard.service.OwnerService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

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
                && (businessDAO.get().getState().equals(BusinessState.Active)
                || businessDAO.get().getState().equals(BusinessState.Pending))) {
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
        if (!businessDAO.isPresent()) {
            Business business = googlePlaceService.getBusiness(externalId);
            User owner = userRepository.save(User
                    .builder()
                    .phoneNumber(phone)
                    .email(email)
                    .build());
            businessDAO = Optional.of(businessRepository.save(
                    businessEntityMapper.toDAOBuilder(business)
                            .state(BusinessState.Pending)
                            .description(request.getDescription())
                            .owner(owner).build()));


        }
        Business claimedBusiness = businessEntityMapper.toModel(businessDAO.get());
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
}
