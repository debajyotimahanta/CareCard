package com.coronacarecard.model;

import java.io.Serializable;
import java.util.Optional;
import java.util.UUID;

@lombok.Builder(toBuilder = true)
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Getter
@lombok.Setter
@lombok.EqualsAndHashCode
public class User implements Serializable {
    private UUID   id;
    private String email;
    private String phoneNumber;
    private Optional<BusinessAccountDetail> businessAccountDetail;
}
