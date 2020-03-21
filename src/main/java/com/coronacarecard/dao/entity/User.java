package com.coronacarecard.dao.entity;

import org.springframework.context.annotation.Lazy;

import javax.persistence.*;
import java.util.List;

@lombok.Builder(toBuilder=true)
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Getter
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
                        name = "idx_phone_number",
                        columnList = "phoneNumber",
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

    @Lazy
    @OneToMany
    @JoinColumn(name="USER_ID")
    private List<GiftCard> cards;
}
