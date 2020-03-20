package com.coronacarecard.config;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AWSConfiguration {
    @Bean
    public AmazonSNS snsClient() {
        return AmazonSNSClientBuilder.standard()
                .withEndpointConfiguration(getSnsEndPointConfig())
                .withCredentials(getAWSCredProvider())
                .build();
    }

    private AwsClientBuilder.EndpointConfiguration getSnsEndPointConfig() {
        // This is for local testing using https://github.com/localstack/localstack
        // TODO Remove this for prod deployment
        return new AwsClientBuilder.EndpointConfiguration("http://localhost:4575", null);
    }

    private AWSCredentialsProvider getAWSCredProvider() {
        return DefaultAWSCredentialsProviderChain.getInstance();
    }
}
