package com.coronacarecard.queue;

import com.amazonaws.services.sqs.model.Message;

public interface SqsMessageHandler {

    boolean handle(Message message);

}
