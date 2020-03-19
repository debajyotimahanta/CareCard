package com.coronacarecard.dao.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Getter
@lombok.ToString
public class BusinessAccountDetails {
    @Id
    @GeneratedValue
    public Long id;
    private String paymentUserId;

}
