package com.coronacarecard.service.payment;

import com.stripe.exception.StripeException;
import com.stripe.model.oauth.TokenResponse;
import com.stripe.net.OAuth;
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
}
