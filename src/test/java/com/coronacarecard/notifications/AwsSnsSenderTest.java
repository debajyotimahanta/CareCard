package com.coronacarecard.notifications;

import com.coronacarecard.model.Business;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import software.amazon.awssdk.services.sns.SnsClient;

import java.util.UUID;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AwsSnsSenderTest {
    @Autowired
    private SnsClient snsClient;

    @Test
    public void sendNotificationTest() {
        final AwsSnsSender<Business> snsSender = new AwsSnsSender<>(snsClient);
        assertTrue("NotificationType topic not found",
                snsClient.listTopics().topics().stream()
                        .anyMatch(t -> t.topicArn().contains(NotificationType.NEW_BUSINESS_REGISTERED.toString())));
        snsSender.sendNotification(NotificationType.NEW_BUSINESS_REGISTERED,
                Business.builder().name(UUID.randomUUID().toString()).build());
    }

}