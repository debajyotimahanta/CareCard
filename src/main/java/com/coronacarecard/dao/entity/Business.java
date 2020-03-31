package com.coronacarecard.dao.entity;

import com.coronacarecard.model.BusinessState;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@lombok.Builder(toBuilder = true)
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Getter
@Entity
@Table(
        uniqueConstraints = @UniqueConstraint(columnNames = {"externalRefId"}),
        indexes = {
                @Index(
                        name = "idx_ext_ref_id",
                        columnList = "externalRefId",
                        unique = true
                ),
                @Index(
                        name = "idx_business_name",
                        columnList = "name"
                )
        }
)
public class Business {

    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", columnDefinition = "uuid")
    private UUID          id;
    private String        externalRefId;
    private Double        latitude;
    private Double        longitude;
    private String        name;
    private String        address;
    private String        description;
    private String        photoUrl;
    private String        photoReference;
    private String        photoAttributions;
    private String        formattedPhoneNumber;
    private String        internationalPhoneNumber;
    private String        Website;
    @Column(length = 32, columnDefinition = "varchar(32) default 'DRAFT'")
    @Enumerated(EnumType.STRING)
    private BusinessState state;

    @ManyToOne
    private User owner;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "business")
    private List<OrderItem> orders;
}
