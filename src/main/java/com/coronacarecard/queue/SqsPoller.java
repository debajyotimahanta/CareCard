package com.coronacarecard.queue;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

@Service
public class SqsPoller {

    private final Logger log = getLogger(getClass());

    private final AmazonSQS sqs;

    //TODO (deba_hook) this is hardocded needs to come from cloudformation
    @Value("${sqs.url}")
    private String queueUrl;

    @Autowired
    private final SqsMessageHandler handler;

    @Autowired
    public SqsPoller(final AmazonSQS sqs, final SqsMessageHandler handler) {
        this.sqs = sqs;
        this.handler = handler;
    }

    @Scheduled(initialDelay = 5 * 1000 , fixedRate = 10 * 1000)
    public void poll() {
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest();
        receiveMessageRequest.setQueueUrl(queueUrl);
        // receiveMessageRequest.setMaxNumberOfMessages(1);

        List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
        log.debug("Found: {} messages.", messages.size());
        messages.stream()
                .filter(handler::handle)
                .forEach(this::deleteMessage);
    }

    private void deleteMessage(final Message message) {
        try {
            sqs.deleteMessage(queueUrl, message.getReceiptHandle());
            log.debug("Message deleted: {}", message.getReceiptHandle());
        } catch (AmazonClientException e) {
            log.warn("Could not delete message {} from queue {}. Reason: {}", message.getMessageId(), queueUrl, e.toString());
        }
    }
}

