package com.coronacarecard.model;

import java.util.UUID;

@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Getter
public class BusinessSearchResult {
    private String name;
    private String address;
    private UUID   id;
    private String externalRefId;
    private Double latitude;
    private Double longitude;
    private boolean alreadyClaimed;
}
