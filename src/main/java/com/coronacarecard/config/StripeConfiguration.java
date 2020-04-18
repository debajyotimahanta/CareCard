package com.coronacarecard.config;

import com.stripe.Stripe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import static com.coronacarecard.config.SecretKey.STRIPE_CLIENT_ID;
import static com.coronacarecard.config.SecretKey.STRIPE_KEY;

@Configuration
public class StripeConfiguration {
    private final SecretsDataStore secretsDataStore;

    @Autowired
    public StripeConfiguration(SecretsDataStore secretsDataStore) {
        this.secretsDataStore = secretsDataStore;
        Stripe.apiKey= secretsDataStore.getValue(STRIPE_KEY);

    }

    public String getClientId(){
        return secretsDataStore.getValue(STRIPE_CLIENT_ID);
    }

    public String getConnectUrl(){
        return "https://connect.stripe.com/express/oauth/authorize?client_id=%1$s&state=%2$s&scope=read_write&response_type=code&redirect_uri=%3$s/payment/stripe/business/confirm";
    }

    public String getWebHookSecret() {
        //TODO (sandeep_hook) inject this from secert data store
        return "whsec_pCr7kUsxp7vu73HuVSmbhYkQCSSBnk7Z";
    }
}
