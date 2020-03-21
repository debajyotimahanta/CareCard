package com.coronacarecard.dao.entity;

import javax.persistence.*;
import java.util.Date;

@lombok.Builder(toBuilder = true)
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Getter
@lombok.ToString
@Entity
@Table(
        name = "giftcards",
        indexes = {
                @Index(
                        name = "idx_ext_ref_id",
                        columnList = "externalRefId",
                        unique = true
                )
        }
)
public class GiftCard {
    @Id
    @GeneratedValue
    private Long id;
    private Date created;
    private Integer amount;
    private String externalRefId;

    @ManyToOne
    @JoinColumn(name="USER_ID", nullable=false)
    private User user;

    @ManyToOne
    @JoinColumn(name="BUSINESS_ID", nullable=false)
    public Business customer;
}
