package com.ikbs.springsecurity.controller;


import com.ikbs.springsecurity.dto.AuthenticationDTO;
import com.ikbs.springsecurity.entite.Utilisateur;
import com.ikbs.springsecurity.service.UtilisateurService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@AllArgsConstructor
@RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class UtilisateurController {

    private final AuthenticationManager authenticationManager;
    private UtilisateurService utilisateurService;

    //@ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path="inscription")
    public void inscription(@RequestBody Utilisateur utilisateur) {
        utilisateurService.inscription(utilisateur);
    }

    @PostMapping(path = "activation")
    public void activation(@RequestBody Map<String,String>activation){
        this.utilisateurService.activation(activation);
    }

    @PostMapping(path = "connexion")
    public Map<String,String> connexion(@RequestBody AuthenticationDTO authenticationDTO) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authenticationDTO.username(),authenticationDTO.password())
        );
       return null;
    }

}
