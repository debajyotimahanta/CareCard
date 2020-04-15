package com.coronacarecard.config;

import com.coronacarecard.exceptions.*;
import com.google.maps.errors.ApiError;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class ApiExceptionControllerAdvice extends ResponseEntityExceptionHandler {
    private static Log log = LogFactory.getLog(ApiExceptionControllerAdvice.class);

    /**
     * Customize the response for HttpMessageNotReadableException.
     * <p>This method delegates to {@link #handleExceptionInternal}.
     *
     * @param ex      the exception
     * @param headers the headers to be written to the response
     * @param status  the selected response status
     * @param request the current request
     * @return a {@code ResponseEntity} instance
     */
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ApiError error = new ApiError();
        error.code = 400;
        error.status = HttpStatus.BAD_REQUEST.toString();
        error.message = "Malformed JSON request";
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InternalException.class)
    protected ResponseEntity<Object> handleInternalExceptions(InternalException exp) {
        log.error("Internal exception occurred", exp);
        ApiError error = new ApiError();
        error.code = 500;
        error.status = HttpStatus.INTERNAL_SERVER_ERROR.toString();
        error.message = "Uh oh! something went wrong processing your request. Please try again or advise the administrator.";
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(OrderNotFoundException.class)
    protected ResponseEntity<Object> orderNotFoundException(OrderNotFoundException exp) {
        log.error("Order Id not found", exp);
        ApiError error = new ApiError();
        error.code = 404;
        error.status = HttpStatus.NOT_FOUND.toString();
        error.message = "Order does not exists";

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BusinessAlreadyClaimedException.class)
    protected ResponseEntity<Object> handleBusinessAlreadyClaimedExceptions(BusinessAlreadyClaimedException exp) {
        log.error("Business has already been claimed", exp);
        ApiError error = new ApiError();
        error.code = 409;
        error.status = HttpStatus.BAD_REQUEST.toString();
        error.message = "Business has already been claimed. Please contact administrator for assistance.";

        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(BusinessClaimException.class)
    protected ResponseEntity<Object> handleBusinessClaimExceptions(BusinessClaimException exp) {
        ApiError error = new ApiError();
        error.code = 400;
        error.status = HttpStatus.BAD_REQUEST.toString();
        error.message = exp.getMessage();

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BusinessNotFoundException.class)
    protected ResponseEntity<Object> handleBusinessNotFoundExceptionExceptions(BusinessNotFoundException exp) {
        ApiError error = new ApiError();
        error.code = 404;
        error.status = HttpStatus.NOT_FOUND.toString();
        error.message = exp.getMessage();

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PaymentServiceException.class)
    protected ResponseEntity<Object> handlePaymentServiceExceptions(PaymentServiceException exp) {
        ApiError error = new ApiError();
        error.code = 400;
        error.status = HttpStatus.BAD_REQUEST.toString();
        error.message = exp.getMessage();

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PaymentAccountNotSetupException.class)
    protected ResponseEntity<Object> handlePaymentAccountNotSetupExceptionExceptions(PaymentAccountNotSetupException exp) {
        ApiError error = new ApiError();
        error.code = 400;
        error.status = HttpStatus.BAD_REQUEST.toString();
        error.message = exp.getMessage();

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

}
