package com.coronacarecard.dao.entity;

import lombok.Builder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Getter
@Entity
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Business business;
    private Double tip;

    @OneToMany(fetch=FetchType.EAGER, mappedBy = "orderItem")
    @Builder.Default
    private List<Item> items = new ArrayList<>();

    @ManyToOne(fetch=FetchType.EAGER)
    private OrderDetail orderDetail;

    public void setOrderDetail(OrderDetail orderDetail) {
        this.orderDetail = orderDetail;
    }

    public void addItem(Item item) {
        items.add(item);
        item.setOrderItem(this);
    }
}
