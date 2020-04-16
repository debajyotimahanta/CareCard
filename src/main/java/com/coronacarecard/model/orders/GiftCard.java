package com.coronacarecard.model.orders;

import com.coronacarecard.model.GiftCardState;

import java.util.UUID;

@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Getter
@lombok.EqualsAndHashCode
public class GiftCard {
    private UUID id;
    private Double amount;
    private GiftCardState state;

}
