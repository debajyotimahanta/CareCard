package com.coronacarecard.service.payment;

import com.coronacarecard.config.StripeConfiguration;
import com.coronacarecard.dao.BusinessAccountDetailRepository;
import com.coronacarecard.dao.BusinessRepository;
import com.coronacarecard.dao.entity.BusinessAccountDetail;
import com.coronacarecard.exceptions.BusinessNotFoundException;
import com.coronacarecard.exceptions.InternalException;
import com.coronacarecard.exceptions.PayementServiceException;
import com.coronacarecard.exceptions.PaymentAccountNotSetupException;
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
import com.stripe.net.OAuth;
import com.stripe.param.checkout.SessionCreateParams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
    private BusinessAccountDetailRepository businessAccountDetailRepository;

    @Autowired
    private BusinessService businessService;

    @Autowired
    @Qualifier("StripeEntityMapper")
    private PaymentEntityMapper paymentMapper;

    @Autowired
    private BusinessEntityMapper businessEntityMapper;

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
        String state;
        state = cryptoService.encrypt(business.getId().toString());
        return String.format(stripeConfiguration.getConnectUrl(), stripeConfiguration.getClientId(), state);
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
            PayementServiceException, InternalException {
        try {
            Optional<com.coronacarecard.dao.entity.Business> businessDAO =
                    businessRepository.findById(Long.valueOf(cryptoService.decrypt(state)));
            if (!businessDAO.isPresent()) {
                throw new BusinessNotFoundException();
            }
            BusinessAccountDetail.BusinessAccountDetailBuilder accountDetailBuilder = businessAccountDetailRepository
                    .findBusiness(businessDAO.get().getId()).getOwner().getAccount().toBuilder();

            Map<String, Object> params = new HashMap<>();
            params.put("grant_type", "authorization_code");
            params.put("code", code);
            TokenResponse response = OAuth.token(params, null);

            accountDetailBuilder.externalRefId(response.getStripeUserId());
            accountDetailBuilder.refreshToken(
                    cryptoService.encrypt(
                            response.getRawJsonObject().get("refresh_token").toString()
                    ));
            accountDetailBuilder.accessToken(
                    cryptoService.encrypt(
                            response.getRawJsonObject().get("access_token").toString()
                    ));
            businessAccountDetailRepository.save(accountDetailBuilder.build());
            return businessEntityMapper.toModel(businessDAO.get());

        } catch (StripeException ex) {
            log.error("Unable to import business", ex);
            throw new PayementServiceException();
        }

    }


}
