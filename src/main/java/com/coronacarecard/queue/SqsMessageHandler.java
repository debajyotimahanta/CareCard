package com.coronacarecard.queue;

import com.amazonaws.services.sqs.model.Message;
import com.coronacarecard.exceptions.InternalException;
import com.coronacarecard.exceptions.PaymentServiceException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.stripe.exception.StripeException;

public interface SqsMessageHandler {

    boolean handle(Message message) throws JsonProcessingException, StripeException, PaymentServiceException, InternalException;

}
