package com.coronacarecard.controller;

import com.coronacarecard.exceptions.BusinessAlreadyClaimedException;
import com.coronacarecard.exceptions.BusinessNotFoundException;
import com.coronacarecard.exceptions.InternalException;
import com.coronacarecard.model.Business;
import com.coronacarecard.model.BusinessRegistrationRequest;
import com.coronacarecard.model.ClaimResult;
import com.coronacarecard.service.CryptoService;
import com.coronacarecard.service.OwnerService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("owner")
@Validated
public class OwnerController {

    private static final Logger       log = LogManager.getLogger(OwnerController.class);
    @Autowired
    private              OwnerService ownerService;

    @Autowired
    private CryptoService cryptoService;

    @PostMapping("/claim")
    public ClaimResult claim(@Valid @RequestBody BusinessRegistrationRequest businessRegistrationRequest)
            throws BusinessNotFoundException, InternalException, BusinessAlreadyClaimedException {

        Business claimedBusiness = ownerService.claimBusiness(businessRegistrationRequest);
        return ClaimResult.builder()
                .business(claimedBusiness)
                .claimToken(claimedBusiness.getId().toString())
                .build();
    }
}
