package com.coronacarecard.model;

import java.io.Serializable;
import java.util.UUID;

@lombok.Builder(toBuilder = true)
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Getter
@lombok.Setter
@lombok.EqualsAndHashCode
public class BusinessAccountDetail implements Serializable {
    public  UUID   id;
    private String externalRefId;
}
