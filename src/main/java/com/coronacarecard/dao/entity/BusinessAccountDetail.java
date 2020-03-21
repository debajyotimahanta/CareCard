package com.coronacarecard.dao.entity;

import javax.persistence.*;

@lombok.Builder(toBuilder = true)
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Getter
@lombok.ToString
@Entity
@Table(
        name = "businessaccountdetails",
        indexes = {
                @Index(
                        name = "idx_ext_ref_id",
                        columnList = "externalRefId",
                        unique = true
                )
        }
)
public class BusinessAccountDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;
    private String externalRefId;

}
