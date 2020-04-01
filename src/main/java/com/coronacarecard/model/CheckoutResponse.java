package com.coronacarecard.model;

@lombok.Builder(toBuilder = true)
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Getter
@lombok.Setter
@lombok.EqualsAndHashCode
public class CheckoutResponse {
    String paymentUrl;
    String sessionId;
}
