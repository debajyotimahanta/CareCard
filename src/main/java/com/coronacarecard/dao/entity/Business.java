package com.coronacarecard.dao.entity;

import com.coronacarecard.model.BusinessState;
import com.coronacarecard.dao.entity.Nominator;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@lombok.Builder(toBuilder = true)
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Getter
// We need this else if we use toBuilder to create different instance of the same object
@lombok.Setter
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
public class Business extends BaseTimeEntity {

    @Id
    @GeneratedValue(generator = "uuid2")
    @Column(name = "id", columnDefinition = "binary(16)")
    private UUID id;
    private String externalRefId;
    private Double latitude;
    private Double longitude;
    private String name;
    private String address;
    @Column(length = 850)
    private String description;
    private String photoUrl;
    private String photoReference;
    private String photoAttributions;
    private String formattedPhoneNumber;
    private String internationalPhoneNumber;
    private String Website;
    private Integer nominations;
    private List<Nominator> nominators;
        
    public void incrementNominations(){
            this.nominations++;
    }
        
    public void addNominator(String email){
            nominations.add( new Nominator(email) );
    }
            
    @Column(length = 32, columnDefinition = "varchar(32) default 'DRAFT'")
    @Enumerated(EnumType.STRING)
    private BusinessState state;

    @ManyToOne(cascade = CascadeType.ALL)
    private User owner;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "business")
    private List<OrderItem> orders;
}
