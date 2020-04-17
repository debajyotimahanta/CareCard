package com.coronacarecard.config;

import com.coronacarecard.exceptions.*;
import com.google.maps.errors.ApiError;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class ApiExceptionControllerAdvice extends ResponseEntityExceptionHandler {
    private static final Logger log = LogManager.getLogger(ApiExceptionControllerAdvice.class);

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
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {
        ApiError error = new ApiError();
        error.code = 400;
        error.status = HttpStatus.BAD_REQUEST.toString();
        error.message = "Malformed JSON request";
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InternalException.class)
    protected ResponseEntity<Object> handleInternalException(InternalException exp) {
            log.error("Internal exception occurred", exp);

        ApiError error = new ApiError();
        error.code = 500;
        error.status = HttpStatus.INTERNAL_SERVER_ERROR.toString();
        error.message = exp.getMessage();
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
    protected ResponseEntity<Object> handleBusinessAlreadyClaimedException(BusinessAlreadyClaimedException exp) {

                log.error("Business has already been claimed", exp);
                ApiError error = new ApiError();
        error.code = 409;
        error.status = HttpStatus.CONFLICT.toString();
        error.message = exp.getMessage();

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
    protected ResponseEntity<Object> handleBusinessNotFoundException(BusinessNotFoundException exp) {
        ApiError error = new ApiError();
        error.code = 404;
        error.status = HttpStatus.NOT_FOUND.toString();
        error.message = exp.getMessage();

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PaymentServiceException.class)
    protected ResponseEntity<Object> handlePaymentServiceException(PaymentServiceException exp) {
        ApiError error = new ApiError();
        error.code = 400;
        error.status = HttpStatus.BAD_REQUEST.toString();
        error.message = exp.getMessage();

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PaymentAccountNotSetupException.class)
    protected ResponseEntity<Object> handlePaymentAccountNotSetupException(PaymentAccountNotSetupException exp) {
        ApiError error = new ApiError();
        error.code = 400;
        error.status = HttpStatus.BAD_REQUEST.toString();
        error.message = exp.getMessage();

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Customize the response for MethodArgumentNotValidException.
     * <p>This method delegates to {@link #handleExceptionInternal}.
     *
     * @param exp      the exception
     * @param headers the headers to be written to the response
     * @param status  the selected response status
     * @param request the current request
     * @return a {@code ResponseEntity} instance
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exp,
                                                                  HttpHeaders headers, HttpStatus status, WebRequest request) {
        Map<String, String> fieldErrorMap = new HashMap<>();
        List<ObjectError>   fieldErrors   = exp.getBindingResult().getAllErrors();
        fieldErrors.forEach((error) -> {
            String errMessage = error.getDefaultMessage();
            String fieldId = ((FieldError) error).getField();
            fieldErrorMap.put(fieldId, errMessage);
        });

        return new ResponseEntity<>(fieldErrorMap, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<Object>  handleConstraintViolationException(ConstraintViolationException exp) {
        Map<String, String>         constraintErrors        = new HashMap<>();
        Set<ConstraintViolation<?>> constraintViolations = exp.getConstraintViolations();
        constraintViolations.forEach((constraintViolation) -> {
            String errMessage = constraintViolation.getMessage();
            String constraintPath = constraintViolation.getPropertyPath().toString();
            constraintErrors.put(constraintPath, errMessage);
        });

        return new ResponseEntity<>(constraintErrors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConversionFailedException.class)
    protected ResponseEntity<Object> handleConversionFailedException(ConversionFailedException exp) {
        ApiError error = new ApiError();
        error.code = 400;
        error.status = HttpStatus.BAD_REQUEST.toString();
        error.message = "Invalid value provided.";

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}