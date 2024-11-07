package com.ikbs.springsecurity.entite;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.Date;

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
    private Instant expired ;
    private String valeur;
}

