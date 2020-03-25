package com.coronacarecard.model.orders;

import com.coronacarecard.model.Currency;

import java.util.List;

@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Getter
public class OrderDetail {
    private Long id;
    private String customerEmail;
    private String customerMobile;
    private List<OrderLine> orderLine;
    private Double total;
    private Double contribution;
    private Double processingFee;
    private Currency currency;
    private OrderStatus status;
}
