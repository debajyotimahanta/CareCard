package com.coronacarecard.model;

import java.io.Serializable;
import java.util.UUID;

@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Getter
public class SuccessPaymentNotification implements Serializable {
    private UUID orderId;
    private String paymentId;

}
