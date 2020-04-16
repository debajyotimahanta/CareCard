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
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    private OrderItem orderItem;

    private Integer quantity;
    private Double unitPrice;

    @OneToMany(fetch=FetchType.EAGER, mappedBy = "item")
    @Builder.Default
    private List<GiftCard> giftCards = new ArrayList<>();

    public void addGiftCard(GiftCard giftCard) {
        giftCards.add(giftCard);
        giftCard.setItem(this);
    }

    public void setOrderItem(OrderItem orderItem) {
        this.orderItem = orderItem;
    }
}
