package com.coronacarecard.fake;

import com.coronacarecard.exceptions.InternalException;
import com.coronacarecard.service.CryptoService;

import java.io.UnsupportedEncodingException;

public class FakeCrypto implements CryptoService {
    public static final String PREFIX = "encrypt_";
    @Override
    public byte[] encrypt(String data) {
        try {
            return (PREFIX +data).getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String decrypt(byte[] data) throws InternalException {
        String state = new String(data);
        return state.replace(PREFIX, "");
    }
}
