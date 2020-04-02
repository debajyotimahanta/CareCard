package com.coronacarecard.dao.entity;

import javax.persistence.*;

@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Getter
@Entity
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    private OrderItem orderItem;

    private Integer quantity;
    private Double unitPrice;

    public void setOrderItem(OrderItem orderItem) {
        this.orderItem = orderItem;
    }
}
