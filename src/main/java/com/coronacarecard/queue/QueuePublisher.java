package com.coronacarecard.queue;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.Serializable;

public interface QueuePublisher<T extends Serializable> {

    void publishEvent(Serializable payload) throws JsonProcessingException;
}
