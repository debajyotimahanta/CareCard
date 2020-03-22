package com.coronacarecard.model;

@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Getter
@lombok.ToString
public class ClaimResult {
    public Business business;
    public String claimToken;
}
