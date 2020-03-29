package com.coronacarecard.service.payment;

import com.coronacarecard.config.StripeConfiguration;
import com.coronacarecard.dao.BusinessRepository;
import com.coronacarecard.exceptions.BusinessNotFoundException;
import com.coronacarecard.exceptions.InternalException;
import com.coronacarecard.exceptions.PaymentAccountNotSetupException;
import com.coronacarecard.mapper.PaymentEntityMapper;
import com.coronacarecard.model.Business;
import com.coronacarecard.model.CheckoutResponse;
import com.coronacarecard.model.PaymentSystem;
import com.coronacarecard.model.orders.OrderDetail;
import com.coronacarecard.service.BusinessService;
import com.coronacarecard.service.CryptoService;
import com.coronacarecard.service.PaymentService;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("StripePaymentService")
public class StripePaymentServiceImpl implements PaymentService {

    @Autowired
    private StripeConfiguration stripeConfiguration;

    @Autowired
    private CryptoService cryptoService;

    @Autowired
    private BusinessRepository businessRepository;

    @Autowired
    private BusinessService businessService;

    @Autowired
    @Qualifier("StripeEntityMapper")
    private PaymentEntityMapper paymentMapper;

    @Override
    public CheckoutResponse successPayment( String urlParams) {
        return null;
    }

    @Override
    public CheckoutResponse failedPayment( String urlParams) {
        return null;
    }

    @Override
    public void confirmTransaction( String transactionId) {

    }

    @Override
    public String generateOnBoardingUrl( Business business) {
        String state;
        state = cryptoService.encrypt(business.getId().toString());
        return String.format(stripeConfiguration.getConnectUrl(), stripeConfiguration.getClientId(), state);
    }

    @Override
    public CheckoutResponse generateCheckoutSession(OrderDetail savedOrder) throws BusinessNotFoundException, PaymentAccountNotSetupException ,InternalException {
        try {
            Session session = Session.create((SessionCreateParams) paymentMapper.toSessionCreateParams(savedOrder, businessService));
            return paymentMapper.toCheckoutResponse(session,savedOrder,businessService);
        } catch (StripeException ex) {
            throw new InternalException(ex.getMessage());
        }
    }

    @Override
    public void validate( OrderDetail order) {

    }

    @Override
    public Business getBusinessDetails( String code) {
        return null;
    }
}
