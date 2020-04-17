package com.coronacarecard.dao.entity;

import com.coronacarecard.model.PaymentState;
import lombok.Builder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Getter
@lombok.Setter
@Entity
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private Long id;

    @ManyToOne
    private Business business;
    private Double tip;

    @OneToMany(fetch=FetchType.EAGER, mappedBy = "orderItem")
    @Builder.Default
    private List<Item> items = new ArrayList<>();

    private PaymentState paymentState;
    private String processingId;

    @ManyToOne(fetch=FetchType.EAGER)
    private OrderDetail orderDetail;

    public void setOrderDetail(OrderDetail orderDetail) {
        this.orderDetail = orderDetail;
    }

    public void addItem(Item item) {
        items.add(item);
        item.setOrderItem(this);
    }

    public Double fundsToTransfer() {
        if (this.tip == null) {
            return getGiftCardTotal();
        }
        return tip + getGiftCardTotal();
    }

    private Double getGiftCardTotal() {
        if(items.size() == 0) {
            return 0.0;
        }
        return items.stream().map(i -> i.getQuantity()*i.getUnitPrice()).mapToDouble(d->d).sum();
    }
}
