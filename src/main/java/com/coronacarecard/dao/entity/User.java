package com.coronacarecard.dao.entity;

import javax.persistence.*;
import java.util.List;

@lombok.Builder(toBuilder = true)
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Getter
@lombok.Setter
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
    @Column(name="id")
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

    @OneToMany(fetch=FetchType.EAGER, mappedBy = "owner", cascade = CascadeType.ALL)
    private List<Business> business;

    @OneToOne(cascade =CascadeType.ALL)
    @JoinColumn(name = "account", referencedColumnName = "id")
    private BusinessAccountDetail account;


}
