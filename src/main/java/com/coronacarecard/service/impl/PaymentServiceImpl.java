package com.coronacarecard.service.impl;

import com.coronacarecard.model.CheckoutResponse;
import com.coronacarecard.model.PaymentSystem;
import com.coronacarecard.service.PaymentService;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {
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
}
