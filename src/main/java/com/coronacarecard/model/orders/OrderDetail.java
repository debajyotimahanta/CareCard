package com.coronacarecard.model.orders;

import com.coronacarecard.model.Currency;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@lombok.Builder(toBuilder = true)
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Getter
@lombok.Setter
@lombok.EqualsAndHashCode
public class OrderDetail implements Serializable {
    private UUID id;
    @NotEmpty
    @NotNull
    @Email
    private String customerEmail;
    private String customerMobile;
    @NotNull
    @NotEmpty
    private List<OrderLine> orderLine;
    @Min(0)
    private Double total;
    @Min(0)
    private Double contribution;
    @Min(0)
    private Double processingFee;
    private Currency currency;
    private OrderStatus status;
}
