package com.coronacarecard.config;

import com.coronacarecard.controller.BusinessController;
import com.coronacarecard.exceptions.BusinessAlreadyClaimedException;
import com.coronacarecard.exceptions.BusinessClaimException;
import com.coronacarecard.exceptions.InternalException;
import com.coronacarecard.exceptions.PaymentServiceException;
import com.coronacarecard.service.BusinessService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
class ApiExceptionControllerAdviceTest {

    private MockMvc mockMvc;

    @Mock
    BusinessService businessService;

    @Autowired
    BusinessController businessController;

    @Autowired
    ApiExceptionControllerAdvice target;

    final String INTERNAL_SERVER_ERROR                      = "Internal Server Error";
    final String PAYMENT_SERVICE_ERROR                      = "Payment service could not process the request";
    final String VENDOR_NOT_REGISTERED_WITH_PAYMENT_SERVICE = "Vendor is not registered with Payment Service";
    final String UNABLE_TO_CLAIM_BUSINESS                   = "Unable to import business. Error retrieving details from Payment Service";

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
        ResponseEntity<Object> result = target.handleInternalException(new InternalException(INTERNAL_SERVER_ERROR));

        // Assert
        assertTrue(result.getStatusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR));
        assertTrue(result.getStatusCodeValue() == 500);
    }

    @Test
    void handleBusinessAlreadyClaimedExceptions() {
        // Act
        ResponseEntity<Object> result = target.handleBusinessAlreadyClaimedException(new BusinessAlreadyClaimedException());

        // Assert
        assertTrue(result.getStatusCode().equals(HttpStatus.CONFLICT));
        assertTrue(result.getStatusCodeValue() == 409);
    }

    @Test
    void handleBusinessClaimExceptions() {
        // Act
        ResponseEntity<Object> result = target.handleBusinessClaimExceptions(new BusinessClaimException(UNABLE_TO_CLAIM_BUSINESS));

        // Assert
        assertTrue(result.getStatusCode().equals(HttpStatus.BAD_REQUEST));
        assertTrue(result.getStatusCodeValue() == 400);
    }

    @Test
    void handlePaymentServiceExceptions() {
        // Act
        ResponseEntity<Object> result = target.handlePaymentServiceException(new PaymentServiceException(PAYMENT_SERVICE_ERROR));

        // Assert
        assertTrue(result.getStatusCode().equals(HttpStatus.BAD_REQUEST));
        assertTrue(result.getStatusCodeValue() == 400);
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