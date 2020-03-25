package com.coronacarecard.model.orders;

@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Getter
public class Item {
    private Integer quantity;
    private Double unitPrice;
}