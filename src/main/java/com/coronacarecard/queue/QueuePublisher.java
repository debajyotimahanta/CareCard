package com.coronacarecard.queue;

import com.google.gson.JsonObject;

public interface QueuePublisher {
    void publishPaymentEvent(JsonObject rawJsonObject);
}
