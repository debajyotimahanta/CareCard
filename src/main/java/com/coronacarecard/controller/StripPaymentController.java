package com.coronacarecard.controller;

import com.coronacarecard.model.CheckoutResponse;
import com.coronacarecard.model.PaymentSystem;
import com.coronacarecard.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("payment/strip")
public class StripPaymentController {
    @Autowired
    private PaymentService paymentService;

    @GetMapping("/payement/stripe/success")
    public CheckoutResponse checkout(String urlParams) {
        return paymentService.successPayment(PaymentSystem.STRIPE, urlParams);
    }


    @GetMapping("/payement/stripe/failure")
    public CheckoutResponse fail(String urlParams) {
        return paymentService.failedPayment(PaymentSystem.STRIPE, urlParams);
    }

    @GetMapping("/payement/stripe/confirm")
    public void confirm(String urlParams) {
        paymentService.confirmTransaction(PaymentSystem.STRIPE, urlParams);
    }
}
