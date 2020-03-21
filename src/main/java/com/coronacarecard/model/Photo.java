package com.coronacarecard.model;

@lombok.Builder(toBuilder=true)
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Getter
@lombok.ToString
public class Photo {
    private String photoReference;
    private String photoUrl;
    // Google subjects that images sources should have relevant attribution displayed to users
    private String[] photoAttributions;
    private int height;
    private int width;
}
