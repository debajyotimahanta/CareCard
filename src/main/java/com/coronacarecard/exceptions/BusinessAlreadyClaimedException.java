package com.coronacarecard.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.CONFLICT, reason="Business already claimed")
public class BusinessAlreadyClaimedException  extends CustomerException{
}
