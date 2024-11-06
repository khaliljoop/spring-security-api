package com.ikbs.springsecurity.repository;

import com.ikbs.springsecurity.entite.Avis;
import com.ikbs.springsecurity.entite.Jwt;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.stream.Stream;

public interface Ijwt extends CrudRepository<Jwt,Integer> {
    Optional<Jwt> findByValeur(String token);


    @Query("from Jwt j where j.deactivated=:deactivated and j.expired=:expired and j.utilisateur.email=:email")
    Optional<Jwt>  findByValeurValideToken(String email, Boolean deactivated, Boolean expired);

    @Query("from Jwt j where j.utilisateur.email=:email")
    Stream<Jwt> findByEmail(String email);


    void deleteAllByExpiredAndDeactivated(boolean expired, boolean deactivated);
}
