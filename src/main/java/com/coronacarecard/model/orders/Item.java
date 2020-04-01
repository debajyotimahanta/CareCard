package com.coronacarecard.model.orders;

import java.io.Serializable;

@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Getter
public class Item implements Serializable {
    private Integer quantity;
    private Double unitPrice;
}