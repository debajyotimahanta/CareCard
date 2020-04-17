package com.coronacarecard.controller;

import com.coronacarecard.exceptions.BusinessNotFoundException;
import com.coronacarecard.exceptions.CustomerException;
import com.coronacarecard.exceptions.InternalException;
import com.coronacarecard.model.PaymentSystem;
import com.coronacarecard.service.OwnerService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("admin")
public class AdminController {
    private static final Logger       log = LogManager.getLogger(AdminController.class);
    @Autowired
    private              OwnerService ownerService;

    @RequestMapping(value = "business/{id}/approve/{paymentSystem}", method = RequestMethod.GET)
    public String approveBusiness(@PathVariable("id") UUID id,
                                  @PathVariable("paymentSystem") PaymentSystem paymentSystem) throws
            CustomerException, InternalException {
        return ownerService.approveClaim(paymentSystem, id);
    }

    // TODO (Deba) refactor the method name.
    @RequestMapping(value = "business/{id}/decline", method = RequestMethod.GET)
    public void approveBusiness(@PathVariable UUID id) throws
            BusinessNotFoundException, InternalException {
        ownerService.declineClaim(id);
    }
}
