package com.coronacarecard.model;

@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Getter
@lombok.Setter
@lombok.ToString
public class Business {
    private String id;
    private Double latitude;
    private Double longitude;
    private String name;
    private String address;
    private Photo  photo;
    private String formattedPhoneNumber;
    private String internationalPhoneNumber;
    private String Website;
}