package com.coronacarecard.service.payment;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.model.oauth.TokenResponse;
import com.stripe.net.OAuth;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class StripeCallsImpl implements StripeCalls {

    @Override
    public TokenResponse token(String code) throws StripeException {
        Map<String, Object> params = new HashMap<>();
        params.put("grant_type", "authorization_code");
        params.put("code", code);
        return OAuth.token(params, null);
    }

    @Override
    public Session generateSession(SessionCreateParams params) throws StripeException {
       return Session.create(params);
    }

    @Override
    public Session retrieveSession(String sessionId) throws StripeException{
        return Session.retrieve(sessionId);
    }

    @Override
    public PaymentIntent retrievePaymentIntent(String paymentIntentId) throws StripeException {
        return PaymentIntent.retrieve(paymentIntentId);
    }
}
