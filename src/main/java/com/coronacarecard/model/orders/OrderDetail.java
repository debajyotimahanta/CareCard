package com.coronacarecard.model.orders;

import com.coronacarecard.model.Currency;

import java.io.Serializable;
import java.util.List;

@lombok.Builder(toBuilder = true)
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Getter
@lombok.Setter
public class OrderDetail implements Serializable {
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
