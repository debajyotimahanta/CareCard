package com.coronacarecard.model;

import java.io.Serializable;

@lombok.Builder(toBuilder = true)
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Getter
@lombok.Setter
@lombok.ToString
public class Business implements Serializable {
    private Long id;
    private String externalRefId;
    private Double latitude;
    private Double longitude;
    private String name;
    private String address;
    private Photo  photo;
    private String formattedPhoneNumber;
    private String internationalPhoneNumber;
    private String Website;
}