package com.coronacarecard.model;

@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Getter
@lombok.ToString
public class Business {
    private String id;
    private Double latitude;
    private Double longitude;
    private String name;
    private String address;
    private String photoUrl;
    private String formattedPhoneNumber;
    private String internationalPhoneNumber;
    private String Website;
}