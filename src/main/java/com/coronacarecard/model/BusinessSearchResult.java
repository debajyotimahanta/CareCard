package com.coronacarecard.model;

@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Getter
@lombok.ToString
public class BusinessSearchResult {
    private String name;
    private String address;
    private String id;
}
