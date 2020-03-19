package com.coronacarecard.model;

@lombok.Builder(toBuilder = true)
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Getter
@lombok.ToString
public class Business {
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
}