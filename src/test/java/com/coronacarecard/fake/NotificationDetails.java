package com.coronacarecard.fake;

import com.coronacarecard.notifications.NotificationType;

import java.io.Serializable;


@lombok.AllArgsConstructor
@lombok.Getter
public class NotificationDetails {
    private NotificationType type;
    private Serializable payload;
}
