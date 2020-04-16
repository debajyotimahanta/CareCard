package com.coronacarecard.queue.impl;

import com.amazonaws.services.sqs.AmazonSQS;
import com.coronacarecard.queue.QueuePublisher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class QueuePublisherImpl implements QueuePublisher {

    @Autowired
    private AmazonSQS sqs;

    @Value("${sqs.url}")
    private String queueUrl;

    private final ObjectMapper objectSerializer = new ObjectMapper()
            .registerModule(new Jdk8Module());


    @Override
    public void publishEvent(Serializable payload) throws JsonProcessingException {
        sqs.sendMessage(queueUrl, objectSerializer.writeValueAsString(payload));

    }
}
