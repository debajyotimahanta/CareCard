package com.coronacarecard.queue.impl;

import com.amazonaws.services.sqs.model.Message;
import com.coronacarecard.dao.entity.OrderDetail;
import com.coronacarecard.exceptions.InternalException;
import com.coronacarecard.exceptions.PaymentServiceException;
import com.coronacarecard.model.SuccessPaymentNotification;
import com.coronacarecard.queue.SqsMessageHandler;
import com.coronacarecard.service.PaymentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.stripe.model.PaymentIntent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class StripeSucessPaymentHandler implements SqsMessageHandler {
    private static Log log = LogFactory.getLog(StripeSucessPaymentHandler.class);
    private final ObjectMapper objectSerializer = new ObjectMapper()
            .registerModule(new Jdk8Module());

    @Autowired
    @Qualifier("StripePaymentService")
    private PaymentService paymentService;

    @Override
    public boolean handle(Message message) {
        log.info("Handling SQS event: " + message.getMessageId());
        SuccessPaymentNotification successPaymentNotification;
        try {
            successPaymentNotification = objectSerializer.readValue(message.getBody(), SuccessPaymentNotification.class);
            paymentService.confirmTransaction(successPaymentNotification.getPaymentId(),
                    successPaymentNotification.getOrderId());
            return true;
        } catch (JsonProcessingException | InternalException | PaymentServiceException e) {
            log.error("Unable to process SQS message", e);
            return false;
        }

    }

    private void validate(Optional<OrderDetail> orderDetails, PaymentIntent paymentIntent) {

    }
}
