package com.coronacarecard.model;

import java.io.Serializable;

@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Getter
@lombok.ToString
public class BusinessApprovalDetails implements Serializable {
    private String registrationUrl;
    private Business business;
}
