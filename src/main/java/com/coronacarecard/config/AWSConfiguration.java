package com.coronacarecard.config;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.encryptionsdk.kms.KmsMasterKeyProvider;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;

@Configuration
public class AWSConfiguration {

    @Value("${MASTER_KEY_ID}")
    private String awsARN;

    @Bean
    public AmazonSNS snsClient() {
        return AmazonSNSClientBuilder.standard()
                .withCredentials(getAWSCredProvider())
                .build();
    }

    private AWSCredentialsProvider getAWSCredProvider() {
        return DefaultAWSCredentialsProviderChain.getInstance();
    }

    @Bean
    @Scope("singleton")
    @Lazy
    public KmsMasterKeyProvider kmsMasterKeyProvider() {
        return KmsMasterKeyProvider.builder().withKeysForEncryption(awsARN).build();
    }
}
