package com.coronacarecard.dao.entity;

import com.coronacarecard.model.Currency;
import com.coronacarecard.model.orders.OrderStatus;
import lombok.Builder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Getter
@lombok.Setter
@Entity
public class OrderDetail {

    @Id
    @GeneratedValue(generator = "uuid2")
    @Column(name = "id", columnDefinition = "binary(16)")
    private UUID id;

    private String customerEmail;
    private String customerMobile;

    @OneToMany(fetch=FetchType.EAGER, mappedBy = "orderDetail", cascade = CascadeType.ALL)
    @Builder.Default
    private List<OrderItem> orderItems = new ArrayList<>();

    private Double total;
    private Double contribution;
    private Double processingFee;
    private Currency currency;
    @Column(length = 32, columnDefinition = "varchar(32) default 'PENDING'")
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    private String sessionId;

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrderDetail(this);

    }
}
