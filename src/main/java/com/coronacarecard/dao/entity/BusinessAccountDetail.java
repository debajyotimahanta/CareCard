package com.coronacarecard.dao.entity;

import javax.persistence.*;

@lombok.Builder(toBuilder = true)
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Getter
@Entity
@Table(
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
    @Column(name="id")
    public  Long   id;
    private String externalRefId;
    @Lob
    private byte[] refreshToken;
    @Lob
    private byte[] accessToken;
}
