package com.coronacarecard.service.payment;

import com.coronacarecard.model.Currency;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.model.oauth.TokenResponse;
import com.stripe.param.checkout.SessionCreateParams;

import java.util.UUID;

public interface StripeCalls {

    TokenResponse token(String code) throws StripeException;

    Session generateSession(SessionCreateParams params) throws StripeException;

    Session retrieveSession(String sessionId) throws StripeException;

    PaymentIntent retrievePaymentIntent(String paymentIntentId) throws StripeException;

    String transferFund(String stripeBusinessId, Double dollarAmount, UUID orderId,
                        Currency currency, String sourceTransactionId) throws StripeException;
}
