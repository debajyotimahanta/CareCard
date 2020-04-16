package com.coronacarecard.config;

import com.stripe.Stripe;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import static com.coronacarecard.config.SecretKey.*;

@Configuration
public class StripeConfiguration {

    private static final Logger           log = LogManager.getLogger(StripeConfiguration.class);
    private final        SecretsDataStore secretsDataStore;

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
        return secretsDataStore.getValue(STRIPE_WEB_HOOK_SECRET);
    }
}
