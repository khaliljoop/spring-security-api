package com.ikbs.springsecurity.entite;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "refresh_jwt")
public class RefreshJwt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private Instant created;
    private Instant expiration;
    private boolean expired;
    private String valeur;
}

