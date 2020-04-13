package com.coronacarecard.model.orders;

import java.io.Serializable;
import java.util.List;

@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Getter
@lombok.EqualsAndHashCode
public class OrderLine  implements Serializable {
    private String businessName;
    private String businessId;
    private List<Item> items;
    private Double     tip;
}

