package com.coronacarecard.dao.entity;

import javax.persistence.*;

@lombok.Builder(toBuilder=true)
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Getter
@lombok.Setter
@lombok.ToString
@Entity
@Table(
        name = "users",
        indexes = {
                @Index(
                        name = "idx_email",
                        columnList = "email",
                        unique = true
                ),
                @Index(
                        name = "idx_phone",
                        columnList = "phone",
                        unique = true
                )
        }
)
public class User {
    @Id
    @GeneratedValue
    private Long id;
    private String email;
    private String phoneNumber;
}
