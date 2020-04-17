package com.coronacarecard.service.payment;

import com.coronacarecard.model.PaymentSystem;
import com.coronacarecard.service.PaymentService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PaymentServiceFactory {
    private static final Logger log = LogManager.getLogger(PaymentServiceFactory.class);
    public static PaymentService createPaymentService(PaymentSystem system){
        switch (system){
            case STRIPE:
            default:
                return new StripePaymentServiceImpl();
        }
    }
}
