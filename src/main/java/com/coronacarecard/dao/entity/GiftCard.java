package com.coronacarecard.dao.entity;

import com.coronacarecard.model.GiftCardState;

import javax.persistence.*;
import java.util.UUID;

@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Getter
@Entity
public class GiftCard extends BaseTimeEntity{

    @Id
    @GeneratedValue(generator = "uuid2")
    @Column(name = "id", columnDefinition = "binary(16)")
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    private Item item;

    private Double amount;

    @Column(length = 32, columnDefinition = "varchar(32) default 'Draft'")
    @Enumerated(EnumType.STRING)
    private GiftCardState state;

    public void setItem(Item item) {
        this.item = item;
    }
}
