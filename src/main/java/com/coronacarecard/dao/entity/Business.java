package com.coronacarecard.dao.entity;

import com.coronacarecard.model.BusinessState;

import java.util.ArrayList;
import javax.persistence.*;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

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
    private int nominations;
    @OneToMany(mappedBy = "business", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Nominator> nominators = new ArrayList<>();
        
    public void incrementNominations(){
            this.nominations++;
    }
        
    public void addNominator(String email){
        Nominator newNominator = new Nominator();
        newNominator.setEmail(email);
        nominators.add(newNominator);
    }
            
    @Column(length = 32, columnDefinition = "varchar(32) default 'DRAFT'")
    @Enumerated(EnumType.STRING)
    private BusinessState state;

    @ManyToOne(cascade = CascadeType.ALL)
    private User owner;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "business")
    private List<OrderItem> orders;
}
