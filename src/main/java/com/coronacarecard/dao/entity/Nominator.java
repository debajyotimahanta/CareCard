package com.coronacarecard.dao.entity;

import javax.persistence.*;
import java.util.UUID;

@lombok.Builder(toBuilder = true)
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Getter
@lombok.Setter
@Entity
public class Nominator {
    @Id
    @GeneratedValue(generator = "uuid2")
    @Column(name = "id", columnDefinition = "binary(16)")
    private UUID id;
    private String email;
    @ManyToOne(cascade = CascadeType.ALL)
    private Business business;
}
