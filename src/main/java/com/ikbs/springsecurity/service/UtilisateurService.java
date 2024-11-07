package com.ikbs.springsecurity.service;


import com.ikbs.springsecurity.entite.Role;
import com.ikbs.springsecurity.entite.TypedeRole;
import com.ikbs.springsecurity.entite.Utilisateur;
import com.ikbs.springsecurity.entite.Validation;
import com.ikbs.springsecurity.repository.IUtilisateur;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
@Service
public class UtilisateurService implements UserDetailsService {
    private static final Logger log = LoggerFactory.getLogger(UtilisateurService.class);
    private IUtilisateur iUtilisateur;
    private BCryptPasswordEncoder passwordEncoder;
    private ValidationService validationService;
    public void inscription(Utilisateur utilisateur){
        if(!utilisateur.getEmail().contains("@") || !utilisateur.getEmail().contains(".")){
            throw new RuntimeException("Email invalid");
        }

        Optional<Utilisateur> user=iUtilisateur.findByEmail(utilisateur.getEmail());
        if(user.isPresent()){
            throw new RuntimeException("votre mail existe deja");
        }
        utilisateur.setMdp(passwordEncoder.encode(utilisateur.getPassword()));
        Role userRole=new Role();
        userRole.setLibelle(TypedeRole.UTILISATEUR);
        utilisateur.setRole(userRole);
       Utilisateur _utilisateur= iUtilisateur.save(utilisateur);
       this.validationService.enregistrer(_utilisateur);

    }

    public void activation(Map<String, String> activation) {
        Validation validation= this.validationService.getValidationByCode(activation.get("code"));
        if(Instant.now().isAfter(validation.getExpired())){
            throw new RuntimeException("Votre code a expiré");
        }
        else{
           Utilisateur _user = this.iUtilisateur.findById(validation.getUtilisateur().getId()).orElseThrow(()->new RuntimeException("Une erreur est survenue"));
           _user.setActif(true);
           this.iUtilisateur.save(_user);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.iUtilisateur.findByEmail(username).orElseThrow(()->new UsernameNotFoundException(username));
    }

    public void modifierMotDePasse(Map<String, String> parametres) {
        Utilisateur utilisateur =(Utilisateur) this.loadUserByUsername(parametres.get("email"));
        log.info("User load: {}",utilisateur);
        this.validationService.enregistrer(utilisateur);

    }

    public void nouveauMotDePasse(Map<String, String> parametres) {
        Utilisateur utilisateur =(Utilisateur) this.loadUserByUsername(parametres.get("email"));
        Validation validation=this.validationService.getValidationByCode(parametres.get("code"));
       if(validation.getUtilisateur().getEmail().equals(utilisateur.getEmail())){
           final String passcrypt=this.passwordEncoder.encode(parametres.get("password"));
           utilisateur.setMdp(passcrypt);
           this.iUtilisateur.save(utilisateur);
       }
    }
}
