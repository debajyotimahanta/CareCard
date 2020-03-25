package com.coronacarecard.service.impl;

import com.coronacarecard.dao.entity.Business;
import com.coronacarecard.dao.entity.OrderDetail;
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

    @Override
    public String generateOnBoardingUrl(PaymentSystem paymentSystem, Business businessDAO) {
        return "TODO";
    }

    @Override
    public CheckoutResponse generateCheckoutSession(OrderDetail savedOrder) {
        return null;
    }

    @Override
    public void validate(PaymentSystem paymentSystem, com.coronacarecard.model.orders.OrderDetail order) {

    }
}
