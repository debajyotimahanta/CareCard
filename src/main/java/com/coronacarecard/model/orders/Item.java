package com.coronacarecard.model.orders;

import java.io.Serializable;
import java.util.List;

@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Getter
@lombok.EqualsAndHashCode
public class Item implements Serializable {
    private Integer quantity;
    private Double unitPrice;
    private List<GiftCard> giftCards;
}