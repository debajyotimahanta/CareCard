package com.coronacarecard.model.orders;

import java.util.List;

@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Getter
public class OrderLine  {
    private String businessName;
    private Long businessId;
    private List<Item> items;
    private Double tip;
}

