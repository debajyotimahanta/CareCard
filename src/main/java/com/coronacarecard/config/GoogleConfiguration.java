package com.coronacarecard.config;

import com.google.maps.GeoApiContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
public class GoogleConfiguration {
    private static final Logger log = LogManager.getLogger(GoogleConfiguration.class);

    @Autowired
    SecretsDataStore secretsDataStore;

    @Bean
    @Lazy
    public GeoApiContext geoApiContext(){
        return new GeoApiContext.Builder()
                .apiKey(secretsDataStore.getValue(SecretKey.GEO_API_KEY))
                .build();
    }
}
