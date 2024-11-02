package com.ikbs.springsecurity.controller;


import com.ikbs.springsecurity.entite.Utilisateur;
import com.ikbs.springsecurity.service.UtilisateurService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;


@AllArgsConstructor
@RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class UtilisateurController {

    private UtilisateurService utilisateurService;

    //@ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path="inscription")
    public void inscription(@RequestBody Utilisateur utilisateur) {
        utilisateurService.inscription(utilisateur);
    }

}
