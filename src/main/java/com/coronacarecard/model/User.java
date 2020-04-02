package com.coronacarecard.model;

import java.io.Serializable;
import java.util.Optional;

@lombok.Builder(toBuilder = true)
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Getter
@lombok.Setter
@lombok.EqualsAndHashCode
public class User implements Serializable {
    private Long   id;
    private String email;
    private String phoneNumber;
    private Optional<BusinessAccountDetail> businessAccountDetail;
}
