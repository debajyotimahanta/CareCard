package com.coronacarecard.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Getter
public class BusinessRegistrationRequest {
    @NotNull
    @NotEmpty
    private String businessId;
    @NotNull
    @NotEmpty
    @Email
    private String email;
    private String phone;
    private String description;
}
