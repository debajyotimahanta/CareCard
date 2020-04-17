package com.coronacarecard.notifications;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.CreateTopicRequest;
import com.amazonaws.services.sns.model.PublishRequest;
import com.coronacarecard.Application;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
    private static final Logger log = LogManager.getLogger(Application.class);

    @Autowired
    private AmazonSNS snsClient;

    private final ObjectMapper objectSerializer = new ObjectMapper()
            .registerModule(new Jdk8Module());

    private final Map<NotificationType, String> topicArns = Maps.newHashMap();

    @VisibleForTesting
    void setSnsClient(final AmazonSNS snsClient) {
        this.snsClient = snsClient;
    }

    @Override
    public void sendNotification(final NotificationType type, final T payload) {
        try {
            log.info(String.format("Publishing notification for %s to %s", type.name(), getTopicArn(type)));
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
