package com.coronacarecard.service.impl;

import com.amazonaws.encryptionsdk.AwsCrypto;
import com.amazonaws.encryptionsdk.CryptoResult;
import com.amazonaws.encryptionsdk.kms.KmsMasterKey;
import com.amazonaws.encryptionsdk.kms.KmsMasterKeyProvider;
import com.coronacarecard.exceptions.InternalException;
import com.coronacarecard.service.CryptoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

@Service
public class CryptoServiceImpl implements CryptoService {

    @Value("${MASTER_KEY_ID}")
    private String awsARN;

    @Autowired
    @Lazy
    KmsMasterKeyProvider keyProvider;

    private final Map<String, String> context = Collections.singletonMap("@rtval", "#postvar");

    @Override
    public String encrypt(String data) {
        AwsCrypto cryptoClient = new AwsCrypto();
        byte[] cipherValue = null;

        try {
            cipherValue = cryptoClient.encryptData(keyProvider, data.getBytes("UTF-8"), context).getResult();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String base64EncodedStr = Base64Utils.encodeToString(cipherValue);
        return base64EncodedStr;
    }

    @Override
    public String decrypt(String state) throws InternalException {
        AwsCrypto cryptoClient = new AwsCrypto();

        CryptoResult<byte[], KmsMasterKey> decryptResult = cryptoClient.decryptData(keyProvider, Base64Utils.decode(state.getBytes()));
        if(!decryptResult.getMasterKeyIds().get(0).equals(awsARN)) {
            throw new InternalException("Wrong key Id returned by KMS.");
        }

        for (final Map.Entry<String, String> e : context.entrySet()) {
            if (!e.getValue().equals(decryptResult.getEncryptionContext().get(e.getKey()))) {
                throw new InternalException("Wrong context key provided by KMS.");
            }
        }

        return new String(decryptResult.getResult(), StandardCharsets.UTF_8);
    }

}
