package com.coronacarecard.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
@Profile("!aws")
public class LocalSecretsDataStore implements SecretsDataStore {
    private static Log log = LogFactory.getLog(LocalSecretsDataStore.class);
    private HashMap<String, String> map = new HashMap<>();

    public LocalSecretsDataStore() {
        log.info("Using Local Secret provider");
        map.put(SecretKey.STRIPE_KEY.name(), "sk_test_K4eOik2NYeiKvHs889qkqe1A007A5S4KJU");
        map.put(SecretKey.STRIPE_CLIENT_ID.name(), "ca_GyeIHG9Q99tzTlnUm141iiZqzJmE5vWq");
        map.put(SecretKey.GEO_API_KEY.name(), "AIzaSyCt-BrVmt0PVN6bAIxABtoGmNVQfjWHM3o");
    }

    @Override
    public String getValue(SecretKey key) {
        return map.get(key.name());
    }
}
