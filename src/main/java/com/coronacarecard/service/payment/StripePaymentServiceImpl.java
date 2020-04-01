package com.coronacarecard.service.payment;

import com.coronacarecard.config.StripeConfiguration;
import com.coronacarecard.dao.BusinessAccountDetailRepository;
import com.coronacarecard.dao.BusinessRepository;
import com.coronacarecard.dao.UserRepository;
import com.coronacarecard.dao.entity.BusinessAccountDetail;
import com.coronacarecard.dao.entity.User;
import com.coronacarecard.exceptions.*;
import com.coronacarecard.mapper.BusinessEntityMapper;
import com.coronacarecard.mapper.PaymentEntityMapper;
import com.coronacarecard.model.Business;
import com.coronacarecard.model.CheckoutResponse;
import com.coronacarecard.model.orders.OrderDetail;
import com.coronacarecard.service.BusinessService;
import com.coronacarecard.service.CryptoService;
import com.coronacarecard.service.PaymentService;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.model.oauth.TokenResponse;
import com.stripe.param.checkout.SessionCreateParams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service("StripePaymentService")
public class StripePaymentServiceImpl implements PaymentService {
    private static Log log = LogFactory.getLog(StripePaymentServiceImpl.class);

    @Autowired
    private StripeConfiguration stripeConfiguration;

    @Autowired
    private CryptoService cryptoService;

    @Autowired
    private BusinessRepository businessRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BusinessService businessService;

    @Autowired
    @Qualifier("StripeEntityMapper")
    private PaymentEntityMapper paymentMapper;

    @Autowired
    private StripeCalls stripeCalls;

    @Autowired
    private BusinessEntityMapper businessEntityMapper;

    @Autowired
    private BusinessAccountDetailRepository businessAccountDetailRepository;

    @Override
    public CheckoutResponse successPayment(String urlParams) {
        return null;
    }

    @Override
    public CheckoutResponse failedPayment(String urlParams) {
        return null;
    }


    @Override
    public void confirmTransaction(String transactionId) {

    }

    @Override
    public String generateOnBoardingUrl(Business business) {

        return String.format(stripeConfiguration.getConnectUrl(),
                stripeConfiguration.getClientId(),
                business.getId().toString());
    }

    @Override
    public CheckoutResponse generateCheckoutSession(OrderDetail savedOrder) throws BusinessNotFoundException, PaymentAccountNotSetupException, InternalException {
        try {
            Session session = Session.create((SessionCreateParams) paymentMapper.toSessionCreateParams(savedOrder, businessService));
            return paymentMapper.toCheckoutResponse(session, savedOrder, businessService);
        } catch (StripeException ex) {
            throw new InternalException(ex.getMessage());
        }
    }

    @Override
    public void validate(OrderDetail order) {

    }

    @Override
    @Transactional
    public Business importBusiness(String code, String state) throws BusinessNotFoundException,
            PayementServiceException, BusinessAlreadyClaimedException {
        try {
            Optional<com.coronacarecard.dao.entity.Business> businessDAO =
                    businessRepository.findById(UUID.fromString(state));
            if (!businessDAO.isPresent()) {
                throw new BusinessNotFoundException();
            }
            com.coronacarecard.dao.entity.Business business = businessDAO.get();
            if (business.getOwner().getAccount() != null) {
                throw new BusinessAlreadyClaimedException();
            }

            User businessOwner = business.getOwner();

            BusinessAccountDetail.BusinessAccountDetailBuilder accountDetailBuilder = BusinessAccountDetail.builder();

            TokenResponse response = stripeCalls.token(code);
            accountDetailBuilder.externalRefId(response.getStripeUserId());
            accountDetailBuilder.refreshToken(
                    cryptoService.encrypt(
                            response.getRawJsonObject().get("refresh_token").getAsString()
                    ));
            accountDetailBuilder.accessToken(
                    cryptoService.encrypt(
                            response.getRawJsonObject().get("access_token").getAsString()
                    ));
            BusinessAccountDetail savedAccountDetails =
                    businessAccountDetailRepository.save(accountDetailBuilder.build());
            businessOwner.setAccount(savedAccountDetails);
            userRepository.save(businessOwner);
            return businessEntityMapper.toModel(businessDAO.get());

        } catch ( StripeException ex) {
            log.error("Unable to import business", ex);
            throw new PayementServiceException();
        }

    }


}
