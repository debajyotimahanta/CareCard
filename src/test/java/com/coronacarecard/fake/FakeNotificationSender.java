package com.coronacarecard.fake;

import com.coronacarecard.notifications.NotificationSender;
import com.coronacarecard.notifications.NotificationType;

import java.io.Serializable;
import java.util.List;

public class FakeNotificationSender implements NotificationSender {
    List<NotificationDetails> allNotifications;

    public FakeNotificationSender(List<NotificationDetails> allNotifications) {
        this.allNotifications = allNotifications;
    }

    @Override
    public void sendNotification(NotificationType type, Serializable payload) {
        //allNotifications.add(new NotificationDetails(type, payload));

    }
}
