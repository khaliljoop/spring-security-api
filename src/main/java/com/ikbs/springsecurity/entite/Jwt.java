package com.ikbs.springsecurity.entite;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "jwt")
public class Jwt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private boolean deactivated = false;
    private boolean expired = false;
    private String valeur;
    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE})
    @JoinColumn(name = "user_id")
    private Utilisateur utilisateur;
    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private RefreshJwt refreshJwt;
}
