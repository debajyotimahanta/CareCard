package com.coronacarecard.config;

import com.stripe.Stripe;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StripeConfiguration {

    private final SecretsDataStore secretsDataStore=new ConfigFileDataStoreImpl();

    @Bean
    public void init(){
        Stripe.apiKey= secretsDataStore.getValue("STRIPE_KEY");
    }

    public String getClientId(){
        return secretsDataStore.getValue("STRIPE_CLIENT_ID");
    }

    public String getConnectUrl(){
        return "https://connect.stripe.com/express/oauth/authorize?client_id=%1$s&state=%2$s&scope=read_write&response_type=code";
    }
}
