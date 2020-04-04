package com.coronacarecard.exceptions;

//@ResponseStatus(value= HttpStatus.NOT_FOUND, reason="No such business")
public class BusinessNotFoundException extends CustomerException {
    public BusinessNotFoundException(String message) { super(message);}
}
