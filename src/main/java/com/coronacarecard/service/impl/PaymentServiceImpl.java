package com.coronacarecard.service.impl;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {
    private static final String STRIPE_CONNECT_URL = "https://connect.stripe.com/oauth/authorize?client_id=%1$s&state=%2$s&scope=read_write&response_type=code";
    private static final String STRIPE_CONNECT_ID="JJJJ";

    @Autowired
    private CryptoService cryptoService;

    @Autowired
    private BusinessRepository businessRepository;

    @Autowired
    private BusinessService businessService;

    @Autowired
    private PaymentEntityMapper paymentMapper;

    @Override
    public CheckoutResponse successPayment(PaymentSystem paymentSystem, String urlParams) {
        return null;
    }

    @Override
    public CheckoutResponse failedPayment(PaymentSystem paymentSystem, String urlParams) {
        return null;
    }

    @Override
    public void confirmTransaction(PaymentSystem paymentSystem, String transactionId) {

    }

    @Override
    public String generateOnBoardingUrl(PaymentSystem paymentSystem, Business business) {
        String url = null;
        String state = null;
        switch (paymentSystem) {
            case STRIPE:
            default:
                state = cryptoService.encrypt(business.getId().toString());
                url=String.format(STRIPE_CONNECT_URL,STRIPE_CONNECT_ID,state);
                break;
        }
        return url;
    }

    @Override
    public CheckoutResponse generateCheckoutSession(OrderDetail savedOrder) throws BusinessNotFoundException, PaymentAccountNotSetupException ,InternalException {
        try {
            Session session = Session.create(paymentMapper.toSessionCreateParams(savedOrder, businessService));
            return paymentMapper.toCheckoutResponse(session,savedOrder,businessService);
        } catch (StripeException ex) {
            throw new InternalException(ex.getMessage());
        }
    }

    @Override
    public void validate(PaymentSystem paymentSystem, com.coronacarecard.model.orders.OrderDetail order) {

    }

    @Override
    public Business getBusinessDetails(PaymentSystem stripe, String code) {
        return null;
    }
}
