package com.coronacarecard.dao.entity;

import javax.persistence.*;

@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Getter
@lombok.ToString
@Entity
@Table(name = "contacts")
public class Contact {
    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    private String formattedPhoneNumber;
    private String internationalPhoneNumber;
    private String Website;
}
