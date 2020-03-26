package com.coronacarecard.service.impl;

import com.coronacarecard.model.Business;
import com.coronacarecard.service.CryptoService;
import org.springframework.stereotype.Service;

@Service
public class CryptoServiceImpl implements CryptoService {
    @Override
    public String encryptBusiness(Business claimedBusiness) {
        return claimedBusiness.getId().toString();
    }

    @Override
    public Long decryptBusiness(String state) {
        return Long.parseLong(state);
    }
}
