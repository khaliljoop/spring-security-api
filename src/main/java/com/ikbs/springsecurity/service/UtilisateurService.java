package com.ikbs.springsecurity.service;


import com.ikbs.springsecurity.entite.Role;
import com.ikbs.springsecurity.entite.TypedeRole;
import com.ikbs.springsecurity.entite.Utilisateur;
import com.ikbs.springsecurity.repository.IUtilisateur;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@AllArgsConstructor
@Service
public class UtilisateurService {
    private IUtilisateur iUtilisateur;
    private BCryptPasswordEncoder passwordEncoder;
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
        iUtilisateur.save(utilisateur);
    }
}
