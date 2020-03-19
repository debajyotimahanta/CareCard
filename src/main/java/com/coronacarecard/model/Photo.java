package com.coronacarecard.model;

@lombok.Builder(toBuilder=true)
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Getter
@lombok.ToString
public class Photo {
    private String photoReference;
    private String photoUrl;
}
