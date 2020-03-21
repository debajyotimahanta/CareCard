package com.coronacarecard.notifications;

import cloud.localstack.LocalstackTestRunner;
import cloud.localstack.TestUtils;
import cloud.localstack.docker.annotation.LocalstackDockerProperties;
import com.coronacarecard.model.Business;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.UUID;

//TODO (biswa) this wont work locally for everyone
@RunWith(LocalstackTestRunner.class)
@LocalstackDockerProperties(services = {"sns"})
@Ignore
public class AwsSnsSenderTest {
    private AwsSnsSender<Business> awsSnsSender;

    @Before
    public void setUp() {
        awsSnsSender = new AwsSnsSender<>();
        awsSnsSender.setSnsClient(TestUtils.getClientSNS());
    }

    @Test
    public void sendNotificationTest() {
        awsSnsSender.sendNotification(NotificationType.NEW_BUSINESS_REGISTERED,
                Business.builder().name(UUID.randomUUID().toString()).build());
    }

}