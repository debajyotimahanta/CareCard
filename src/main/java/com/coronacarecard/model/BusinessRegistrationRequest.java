package com.coronacarecard.model;

@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Getter
@lombok.ToString
public class BusinessRegistrationRequest {
    private String businessId;
    private String email;
    private String phone;
    private String description;
    
}
