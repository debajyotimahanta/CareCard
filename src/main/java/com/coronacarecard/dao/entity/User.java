package com.coronacarecard.dao.entity;

import javax.persistence.*;
import java.util.List;

@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Getter
@Entity
@Table(
        name = "users",
        uniqueConstraints = @UniqueConstraint(columnNames = {"email"}),
        indexes = {
                @Index(
                        name = "idx_email",
                        columnList = "email",
                        unique = true
                ),
                @Index(
                        name = "idx_confirmation_token",
                        columnList = "confirmation_token"
                )
        }
)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "phoneNumber", nullable = false)
    private String phoneNumber;

    @Transient
    private String password;

    @Column(name = "enabled")
    private boolean enabled;

    @Column(name = "confirmation_token")
    private String confirmationToken;

    @OneToMany(fetch=FetchType.EAGER, mappedBy = "owner")
    private List<Business> business;

    @OneToOne(cascade =CascadeType.ALL)
    @JoinColumn(name = "id", referencedColumnName = "id")
    private BusinessAccountDetail account;


}
