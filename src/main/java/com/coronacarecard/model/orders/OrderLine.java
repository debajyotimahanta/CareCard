package com.coronacarecard.model.orders;

import java.util.List;
import java.util.UUID;

@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Getter
public class OrderLine  {
    private String     businessName;
    private UUID       businessId;
    private List<Item> items;
    private Double     tip;
}

