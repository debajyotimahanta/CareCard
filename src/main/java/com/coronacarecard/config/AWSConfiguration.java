package com.coronacarecard.config;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.encryptionsdk.kms.KmsMasterKeyProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
public class AWSConfiguration {

    @Value("${MASTER_KEY_ID}")
    private String awsARN;

    @Bean
    @Autowired
    public AmazonSNS snsClient(AWSCredentialsProvider awsCredentialsProvider) {
        return AmazonSNSClientBuilder.standard()
                .withCredentials(awsCredentialsProvider)
                .build();
    }

    @Bean
    public AWSCredentialsProvider getAWSCredProvider() {
        return DefaultAWSCredentialsProviderChain.getInstance();
    }

    @Bean
    @Lazy
    @Autowired
    public KmsMasterKeyProvider kmsMasterKeyProvider(AWSCredentialsProvider awsCredentialsProvider) {

        return KmsMasterKeyProvider
                .builder()
                .withCredentials(awsCredentialsProvider)
                .withKeysForEncryption(awsARN).build();
    }

    @Bean
    @Lazy
    @Autowired
    public AmazonS3 amazonS3Client(AWSCredentialsProvider awsCredentialsProvider) {
        return  AmazonS3ClientBuilder.standard()
                .withCredentials(awsCredentialsProvider)
                .build();
    }
}
