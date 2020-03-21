package com.coronacarecard.controller;

import com.coronacarecard.exceptions.BusinessAlreadyClaimedException;
import com.coronacarecard.exceptions.BusinessNotFoundException;
import com.coronacarecard.exceptions.InternalException;
import com.coronacarecard.model.Business;
import com.coronacarecard.model.BusinessRegistrationRequest;
import com.coronacarecard.model.ClaimResult;
import com.coronacarecard.service.CryptoService;
import com.coronacarecard.service.OwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("owner")
public class OwnerController {

    @Autowired
    private OwnerService ownerService;

    @Autowired
    private CryptoService cryptoService;


    @PostMapping("/register")
    public ClaimResult register(@RequestBody BusinessRegistrationRequest businessRegistrationRequest)
            throws BusinessNotFoundException, InternalException, BusinessAlreadyClaimedException {

        Business claimedBusiness = ownerService.claimBusiness(businessRegistrationRequest.getBusinessId(),
                businessRegistrationRequest.getEmail(), businessRegistrationRequest.getPhone());
        return ClaimResult.builder()
                .business(claimedBusiness)
                .encryptedState(cryptoService.encryptBusiness(claimedBusiness))
                .build();

    }



}
