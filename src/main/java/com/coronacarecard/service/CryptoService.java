package com.coronacarecard.service;


import com.coronacarecard.model.Business;

public interface CryptoService {
    /**
     * This takes import info about the Business and encrypts it and generates a string.
     * This string is passed to the payment registration form, which is passed back to us
     * when registration is compeleted. We will use this state to verify key info and
     * avoid CSRF attacks.
     * @param claimedBusiness
     * @return
     */
    String encryptBusiness(Business claimedBusiness);
}
