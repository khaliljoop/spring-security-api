package com.ikbs.springsecurity.service;


import com.ikbs.springsecurity.entite.Avis;
import com.ikbs.springsecurity.entite.Utilisateur;
import com.ikbs.springsecurity.repository.Iavis;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class AvisService {
    private final Iavis iavis;
    public void createAvis(Avis avis) {
        Utilisateur utilisateur=(Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        avis.setUtilisateur(utilisateur);
        iavis.save(avis);
    }
}
