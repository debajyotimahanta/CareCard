package com.coronacarecard.dao.entity;

import javax.persistence.*;
import java.util.List;

@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Getter
@lombok.ToString
@Entity
@Table(name = "users")
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

    @OneToOne
    @JoinColumn(name = "id", referencedColumnName = "id")
    private BusinessAccountDetail account;


}
