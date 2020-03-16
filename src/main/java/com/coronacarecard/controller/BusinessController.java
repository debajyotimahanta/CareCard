package com.coronacarecard.controller;

import com.coronacarecard.exceptions.BusinessNotFoundException;
import com.coronacarecard.exceptions.InternalException;
import com.coronacarecard.model.Business;
import com.coronacarecard.service.BusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BusinessController {

    @Autowired
    private BusinessService businessService;

    @GetMapping("/import")
    public Business create(@RequestParam(value = "googleplaceid") String googlePlaceId) throws BusinessNotFoundException, InternalException {
        return businessService.create(googlePlaceId);
    }

}
