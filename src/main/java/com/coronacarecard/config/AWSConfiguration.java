package com.coronacarecard.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.services.sns.SnsClient;

import java.net.URI;

@Configuration
public class AWSConfiguration {
    @Bean
    public SnsClient snsClient() {
        return SnsClient.builder()
                .credentialsProvider(getAWSCredProvider())
                // Note: Local testing can be done with https://github.com/s12v/sns
                //.endpointOverride(URI.create("http://localhost:9911"))
                .build();
    }

    private AwsCredentialsProvider getAWSCredProvider() {
        return DefaultCredentialsProvider.create();
    }
}
