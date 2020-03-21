package com.coronacarecard.notifications;

import java.io.Serializable;

public interface NotificationSender<T extends Serializable> {

    /**
     * Send payload of specified type as a notification
     *
     * @param type Notification Type Enum
     * @param payload Serializable notification payload
     */
    void sendNotification(NotificationType type, T payload);

}
