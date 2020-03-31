package com.coronacarecard.dao.entity;

import javax.persistence.*;
import java.util.UUID;

@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Getter
@Entity
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    private OrderItem orderItem;

    private Integer quantity;
    private Double unitPrice;

    public void setOrderItem(OrderItem orderItem) {
        this.orderItem = orderItem;
    }
}
