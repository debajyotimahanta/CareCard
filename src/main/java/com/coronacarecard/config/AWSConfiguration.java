package com.coronacarecard.config;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.encryptionsdk.kms.KmsMasterKeyProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;

@Configuration
public class AWSConfiguration {

    @Value("${MASTER_KEY_ID}")
    private String awsARN;

    @Autowired
    private Environment environment;

    @Bean
    @Autowired
    public AmazonSNS snsClient(AWSCredentialsProvider awsCredentialsProvider) {
        return AmazonSNSClientBuilder.standard()
                .withCredentials(awsCredentialsProvider)
                .build();
    }

    @Bean
    public AWSCredentialsProvider getAWSCredProvider() {
        if (isAWS()) {
            return DefaultAWSCredentialsProviderChain.getInstance();
        } else {
            return new ProfileCredentialsProvider("corona_card_dev");
        }
    }

    private boolean isAWS() {
        String[] activeProfiles = environment.getActiveProfiles();
        for (int i = 0; i < activeProfiles.length; i++) {
            if (activeProfiles[i].equalsIgnoreCase("aws")) {
                return true;
            }

        }
        return false;
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
    public AWSSecretsManager awsSecretsManager(AWSCredentialsProvider awsCredentialsProvider) {
        return AWSSecretsManagerClientBuilder.standard()
                .withCredentials(awsCredentialsProvider)
                .build();
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
