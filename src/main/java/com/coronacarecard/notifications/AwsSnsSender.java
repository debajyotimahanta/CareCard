package com.coronacarecard.notifications;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.CreateTopicRequest;
import com.amazonaws.services.sns.model.PublishRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import lombok.SneakyThrows;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Map;

/**
 * This class takes a {#Serializable} and posts it to an AWS SNS topic named
 * after the specified notification type
 */
@Component
public class AwsSnsSender<T extends Serializable> implements NotificationSender<T> {
    private static Log log = LogFactory.getLog(AwsSnsSender.class);

    @Autowired
    private AmazonSNS snsClient;

    private final ObjectMapper objectSerializer = new ObjectMapper();

    private final Map<NotificationType, String> topicArns = Maps.newHashMap();

    @VisibleForTesting
    void setSnsClient(final AmazonSNS snsClient) {
        this.snsClient = snsClient;
    }

    @Override
    public void sendNotification(final NotificationType type, final T payload) {
        try {
            snsClient.publish(new PublishRequest()
                            .withTopicArn(getTopicArn(type))
                            .withSubject(payload.getClass().getSimpleName())
                            .withMessage(getMessage(payload))
            );
        } catch (SdkClientException ex) {
            log.error("For local testing, spin up a stack using https://github.com/localstack/localstack", ex);
        }

    }

    private String getTopicArn(final NotificationType type) {
        if (!topicArns.containsKey(type)) {
            try {
                topicArns.put(type, snsClient.createTopic(
                        new CreateTopicRequest()
                                .withName(type.toString()))
                        .getTopicArn());
            } catch (SdkClientException ex) {
                log.error("For local testing, spin up a stack using https://github.com/localstack/localstack", ex);
            }
        }
        return topicArns.get(type);
    }

    @SneakyThrows
    private String getMessage(final T payload) {
        return objectSerializer.writeValueAsString(payload);
    }
}
