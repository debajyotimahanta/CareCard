package com.coronacarecard.model;

import java.io.Serializable;

@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Getter
public class BusinessApprovalDetails implements Serializable {
    private String registrationUrl;
    private Business business;
}
