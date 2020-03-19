package com.coronacarecard.dao.entity;

import javax.persistence.*;
import java.util.Date;

@lombok.Builder(toBuilder = true)
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Getter
@lombok.Setter
@lombok.ToString
@Entity
@Table(
        name = "giftcards",
        indexes = {
                @Index(
                        name = "idx_ext_ref_id",
                        columnList = "external_ref_id",
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
    private String externalREfId;
    private User user;
}
