package com.coronacarecard.service;


import com.coronacarecard.exceptions.InternalException;

public interface CryptoService {
    /**
     * This takes import info about the Business and encrypts it and generates a string.
     * This string is passed to the payment registration form, which is passed back to us
     * when registration is completed. We will use this state to verify key info and
     * avoid CSRF attacks.
     * @param data
     * @return
     */
    String encrypt(String data);

    String decrypt(String state) throws InternalException;
}