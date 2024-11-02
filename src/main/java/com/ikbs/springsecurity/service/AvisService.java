package com.ikbs.springsecurity.service;


import com.ikbs.springsecurity.entite.Avis;
import com.ikbs.springsecurity.repository.Iavis;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class AvisService {
    private final Iavis iavis;
    public Avis createAvis(Avis avis) {
        return iavis.save(avis);
    }
}
