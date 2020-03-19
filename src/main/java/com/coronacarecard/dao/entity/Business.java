package com.coronacarecard.dao.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

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
    private String photoReference;
    private String formattedPhoneNumber;
    private String internationalPhoneNumber;
    private String Website;
}
