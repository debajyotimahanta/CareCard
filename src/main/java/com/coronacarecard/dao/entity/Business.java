package com.coronacarecard.dao.entity;

import javax.persistence.*;

@lombok.Builder(toBuilder=true)
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Getter
@lombok.ToString
@Entity
@Table(name = "businesses")
public class Business {

    @Id
    private String id;

    private Double latitude;
    private Double longitude;
    private String name;
    private String address;
    private String photoUrl;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "contact_id", referencedColumnName = "id")
    private Contact contact;
}
