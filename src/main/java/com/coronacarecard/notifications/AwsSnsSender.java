package com.coronacarecard.notifications;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import lombok.SneakyThrows;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.CreateTopicRequest;
import software.amazon.awssdk.services.sns.model.PublishRequest;

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
    private final SnsClient snsClient;

    private final ObjectMapper objectSerializer;

    private final Map<NotificationType, String> topicArns;

    public AwsSnsSender(final SnsClient snsClient) {
        this.snsClient = snsClient;
        this.objectSerializer = new ObjectMapper();
        this.topicArns = Maps.newHashMap();
    }

    @Override
    public void sendNotification(final NotificationType type, final T payload) {
        try {
            snsClient.publish(
                    PublishRequest.builder()
                            .topicArn(getTopicArn(type))
                            .subject(payload.getClass().getSimpleName())
                            .message(getMessage(payload))
                            .build()
            );
        } catch (Exception ex) {
            log.error(ex);
            // TODO (biswa):dont fail silently do something but first make it work in desktop
        }

    }

    private String getTopicArn(final NotificationType type) {
        if (!topicArns.containsKey(type)) {
            topicArns.put(type, snsClient.createTopic(
                    CreateTopicRequest.builder()
                            .name(type.toString())
                            .build())
                    .topicArn());
        }
        return topicArns.get(type);
    }

    @SneakyThrows
    private String getMessage(final T payload) {
        return objectSerializer.writeValueAsString(payload);
    }
}
