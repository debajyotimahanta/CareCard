package com.coronacarecard.config;

import com.coronacarecard.exceptions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
class ApiExceptionControllerAdviceTest {

    @Autowired
    ApiExceptionControllerAdvice target;

    final String INTERNAL_SERVER_ERROR    = "Internal Server Error";
    final String PAYMENT_SERVICE_ERROR       = "Payment service could not process the request";
    final String VENDOR_NOT_REGISTERED_WITH_PAYMENT_SERVICE         = "Vendor is not registered with Payment Service";
    final String UNABLE_TO_CLAIM_BUSINESS = "Unable to import business. Error retrieving details from Payment Service";

    void setUp() throws Exception {

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void handleHttpMessageNotReadable() {
    }

    @Test
    void handleInternalExceptions() {
        // Act
        ResponseEntity<Object> result = target.handleInternalExceptions(new InternalException(INTERNAL_SERVER_ERROR));

        // Assert
        assertTrue(result.getStatusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR));
        assertTrue(result.getStatusCodeValue() == 500);
    }

    @Test
    void handleBusinessAlreadyClaimedExceptions() {
        // Act
        ResponseEntity<Object> result = target.handleBusinessAlreadyClaimedExceptions(new BusinessAlreadyClaimedException());

        // Assert
        assertTrue(result.getStatusCode().equals(HttpStatus.BAD_REQUEST));
        assertTrue(result.getStatusCodeValue() == 400);
    }

    @Test
    void handleBusinessClaimExceptions() {
        // Act
        ResponseEntity<Object> result = target.handleBusinessClaimExceptions(new BusinessClaimException(UNABLE_TO_CLAIM_BUSINESS));

        // Assert
        assertTrue(result.getStatusCode().equals(HttpStatus.BAD_REQUEST));
        assertTrue(result.getStatusCodeValue() == 400);
    }

//    @Test
//    void handleCustomerExceptions() {
//        // Act
//        ResponseEntity<Object> result = target.handleCustomerExceptions(new CustomerException(INTERNAL_SERVER_ERROR));
//
//        // Assert
//        assertTrue(result.getStatusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR));
//        assertTrue(result.getStatusCodeValue() == 500);
//    }

    @Test
    void handlePaymentServiceExceptions() {
        // Act
        ResponseEntity<Object> result = target.handlePaymentServiceExceptions(new PaymentServiceException(PAYMENT_SERVICE_ERROR));

        // Assert
        assertTrue(result.getStatusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR));
        assertTrue(result.getStatusCodeValue() == 500);
    }

    @Test
    void handlePaymentAccountNotSetupExceptionExceptions() {
        // Act
        ResponseEntity<Object> result = target.handleBusinessClaimExceptions(new BusinessClaimException(VENDOR_NOT_REGISTERED_WITH_PAYMENT_SERVICE));

        // Assert
        assertTrue(result.getStatusCode().equals(HttpStatus.BAD_REQUEST));
        assertTrue(result.getStatusCodeValue() == 400);
    }
}