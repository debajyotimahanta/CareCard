package com.coronacarecard.exceptions;

public class CustomerException extends Exception {
    public CustomerException() {
    }

    public CustomerException(String message) {
        super(message);
    }
}
