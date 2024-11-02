package com.ikbs.springsecurity.repository;

import com.ikbs.springsecurity.entite.Utilisateur;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface IUtilisateur extends CrudRepository<Utilisateur,Integer> {
    Optional<Utilisateur> findByEmail(String email);
}
