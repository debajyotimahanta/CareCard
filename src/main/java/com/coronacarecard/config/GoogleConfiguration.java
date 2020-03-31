package com.coronacarecard.config;

import com.google.maps.GeoApiContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
public class GoogleConfiguration {
    @Value("${google.geoapi.key}")
    private String geoApiKey;


    @Bean
    @Lazy
    public GeoApiContext geoApiContext(){
        return new GeoApiContext.Builder()
                .apiKey(geoApiKey)
                .build();
    }
}
