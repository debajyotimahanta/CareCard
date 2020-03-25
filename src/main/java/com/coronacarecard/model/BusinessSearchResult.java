package com.coronacarecard.model;

@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Getter
public class BusinessSearchResult {
    private String name;
    private String address;
    private Long id;
    private String externalRefId;
    private Double latitude;
    private Double longitude;
}
