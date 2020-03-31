package com.coronacarecard.config;

import org.springframework.stereotype.Component;

@Component
public class ConfigFileDataStoreImpl implements SecretsDataStore {

    @Override
    public String getValue(String Key) {
        switch (Key){
            case "STRIPE_KEY":
                return "sk_test_K4eOik2NYeiKvHs889qkqe1A007A5S4KJU";
            case "STRIPE_CLIENT_ID":
                return "ca_GyeIHG9Q99tzTlnUm141iiZqzJmE5vWq";
        }
        return null;
    }
}
