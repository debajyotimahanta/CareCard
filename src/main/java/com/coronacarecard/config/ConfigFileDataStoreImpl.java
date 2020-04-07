package com.coronacarecard.config;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
@Profile("aws")
public class ConfigFileDataStoreImpl implements SecretsDataStore {
    private static Log log = LogFactory.getLog(ConfigFileDataStoreImpl.class);
    private static final String SECRET_NAME = "CCCAppSecret";
    private final HashMap<String, String> secretMap;

    @Autowired
    public ConfigFileDataStoreImpl(AWSSecretsManager awsSecretsManager) throws JsonProcessingException {
        log.info("Using AWS Secret provider");
        GetSecretValueResult getSecretValueResult =
                awsSecretsManager.getSecretValue(new GetSecretValueRequest().withSecretId(SECRET_NAME));
        log.info("Using sercret :" + getSecretValueResult.getARN());
        final ObjectMapper objectMapper = new ObjectMapper();
        secretMap = objectMapper.readValue(getSecretValueResult.getSecretString(), HashMap.class);
        log.info("Got result from AWS ");
    }

    @Override
    public String getValue(SecretKey key) {
        return secretMap.get(key.name());
    }
}
