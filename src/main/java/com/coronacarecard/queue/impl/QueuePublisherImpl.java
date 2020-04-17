package com.coronacarecard.queue.impl;

import com.coronacarecard.queue.QueuePublisher;
import com.google.gson.JsonObject;
import org.springframework.stereotype.Component;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

@Component
public class QueuePublisherImpl implements QueuePublisher {
    //TODO (sandeep_hook) This will publish the event to the sqs queue as it is i guess, ultimately it will
    // be consumed by {@link SqsPoller}
    @Override
    public void publishPaymentEvent(JsonObject rawJsonObject) {
        throw new NotImplementedException();

    }
}
