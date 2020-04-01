package com.coronacarecard.service.payment;

import com.stripe.exception.StripeException;
import com.stripe.model.oauth.TokenResponse;

public interface StripeCalls {

    TokenResponse token(String code) throws StripeException;
}
