package com.coronacarecard.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;

@Configuration
public class AWSConfiguration {
    @Bean
    public SnsClient snsClient() {
        return SnsClient.builder()
                .credentialsProvider(getAWSCredProvider())
                //TODO Fix this so that meta data service works on desktop and we dont have to hardcode this
                .region(Region.US_EAST_2)
                // Note: Local testing can be done with https://github.com/s12v/sns
                //.endpointOverride(URI.create("http://localhost:9911"))
                .build();
    }

    private AwsCredentialsProvider getAWSCredProvider() {
        return DefaultCredentialsProvider.create();
    }
}
