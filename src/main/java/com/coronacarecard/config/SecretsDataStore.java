package com.coronacarecard.config;

public interface SecretsDataStore {
    String getValue(SecretKey Key);
}
