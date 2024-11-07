package com.ikbs.springsecurity.repository;

import com.ikbs.springsecurity.entite.Validation;
import org.springframework.data.repository.CrudRepository;

import java.time.Instant;
import java.util.Optional;

public interface IValidation extends CrudRepository<Validation,Integer> {

    Optional<Validation> findByCode(String code);
    void deleteAllByExpiredBefore(Instant now);
}
