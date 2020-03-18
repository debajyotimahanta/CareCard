package com.coronacarecard.dao.entity;

@lombok.Builder(toBuilder=true)
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Getter
@lombok.ToString
public class Photo {
    private String photoReference;
    private String photo;
}
