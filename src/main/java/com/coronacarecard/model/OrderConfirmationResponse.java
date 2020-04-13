package com.coronacarecard.model;

import com.coronacarecard.model.orders.OrderDetail;

@lombok.Builder(toBuilder = true)
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Getter
public class OrderConfirmationResponse {
    private OrderDetail orderDetail;
}
