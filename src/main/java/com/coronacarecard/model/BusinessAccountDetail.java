package com.coronacarecard.model;

import java.io.Serializable;

@lombok.Builder(toBuilder = true)
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Getter
@lombok.Setter
@lombok.EqualsAndHashCode
public class BusinessAccountDetail implements Serializable {
    public  Long   id;
    private String externalRefId;
}
