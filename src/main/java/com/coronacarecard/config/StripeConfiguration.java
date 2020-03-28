package com.coronacarecard.config;

import com.stripe.Stripe;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StripeConfiguration {

    @Value("${STRIPE_KEY:sk_test_K4eOik2NYeiKvHs889qkqe1A007A5S4KJU}")
    private String STRIPE_KEY;

    @Bean
    public void init(){
        Stripe.apiKey=STRIPE_KEY;
    }
}
