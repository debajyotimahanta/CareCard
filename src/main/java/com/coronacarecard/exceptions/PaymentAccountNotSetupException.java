package com.coronacarecard.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.UNPROCESSABLE_ENTITY, reason="Payment account not setup for the business")
public class PaymentAccountNotSetupException extends CustomerException {
}
