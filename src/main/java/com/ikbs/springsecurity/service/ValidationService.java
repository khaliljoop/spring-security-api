package com.ikbs.springsecurity.service;


import com.ikbs.springsecurity.entite.Utilisateur;
import com.ikbs.springsecurity.entite.Validation;
import com.ikbs.springsecurity.repository.IValidation;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;

import static java.time.temporal.ChronoUnit.MINUTES;

@Transactional
@AllArgsConstructor
@Service
public class ValidationService {
    private static final Logger log = LoggerFactory.getLogger(ValidationService.class);
    private final IValidation iValidation;
    private final NotificationService notificationService;

    public void enregistrer(Utilisateur utilisateur) {
        Validation validation=new Validation();
        Instant created = Instant.now();
        Instant expires = Instant.now().plus(10, MINUTES);
        validation.setUtilisateur(utilisateur);
        validation.setCreated(created);
        validation.setExpired(expires);

        Random r = new Random();
        int randomNumber = r.nextInt(999999);

        String code = String.format("%06d", randomNumber);
        validation.setCode(code);
        Validation v=this.iValidation.save(validation);
        if(v.getId()>0){
            this.notificationService.envoyer(v);
        }
    }

    public Validation getValidationByCode(String code) {
        return this.iValidation.findByCode(code).orElseThrow(()->new RuntimeException(""));
    }

    // lien util : https://crontab.guru/
    @Scheduled(cron = "0 */1 * * * *")
    public void cleanTable() {

        log.info("clean tab {}", Instant.now());
        this.iValidation.deleteAllByExpiredBefore(Instant.now());
    }
}
