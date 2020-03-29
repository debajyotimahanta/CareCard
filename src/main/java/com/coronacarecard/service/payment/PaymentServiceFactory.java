package com.coronacarecard.service.payment;

import com.coronacarecard.model.PaymentSystem;
import com.coronacarecard.service.PaymentService;

public class PaymentServiceFactory {
    public static PaymentService createPaymentService(PaymentSystem system){
        switch (system){
            case STRIPE:
            default:
                return new StripePaymentServiceImpl();
        }
    }
}
