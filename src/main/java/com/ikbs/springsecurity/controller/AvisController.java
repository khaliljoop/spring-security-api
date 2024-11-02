package com.ikbs.springsecurity.controller;


import com.ikbs.springsecurity.entite.Avis;
import com.ikbs.springsecurity.service.AvisService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@AllArgsConstructor
@RequestMapping("avis")
@RestController
public class AvisController {

    private final AvisService avisService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public void create(@RequestBody Avis avis) {
        avisService.createAvis(avis);
    }
}
