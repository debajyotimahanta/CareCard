package com.coronacarecard.dao.entity;

import com.coronacarecard.model.BusinessState;
import org.springframework.context.annotation.Lazy;

import javax.persistence.*;
import java.util.List;

@lombok.Builder(toBuilder=true)
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Getter
@lombok.Setter
@lombok.ToString
@Entity
@Table(
        name = "businesses",
        indexes = {
                @Index(
                        name = "idx_ext_ref_id",
                        columnList = "external_ref_id",
                        unique = true
                ),
                @Index(
                        name = "idx_name",
                        columnList = "name"
                )
        }
)
public class Business {

    @Id
    @GeneratedValue
    private Long id;
    private String externalRefId;
    private Double latitude;
    private Double longitude;
    private String name;
    private String address;
    private String photoUrl;
    private String formattedPhoneNumber;
    private String internationalPhoneNumber;
    private String Website;
    @Column(length = 32, columnDefinition = "varchar(32) default 'DRAFT'")
    @Enumerated(EnumType.STRING)
    private BusinessState state;


    @OneToOne
    @JoinColumn(name = "account_id", referencedColumnName = "id")
    private BusinessAccountDetails account;

    @Lazy
    private List<GiftCard> purchasedCards;
}
