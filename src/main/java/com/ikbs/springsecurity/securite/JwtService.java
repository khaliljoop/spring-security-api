package com.ikbs.springsecurity.securite;


import com.ikbs.springsecurity.constantes.Constantes;
import com.ikbs.springsecurity.entite.Jwt;
import com.ikbs.springsecurity.entite.RefreshJwt;
import com.ikbs.springsecurity.entite.Utilisateur;
import com.ikbs.springsecurity.repository.Ijwt;
import com.ikbs.springsecurity.service.UtilisateurService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Transactional
@AllArgsConstructor
@Service
public class JwtService {

    private static final Logger log = LoggerFactory.getLogger(JwtService.class);
    public static final String REFRESH_JWT = "refreshJwt";
    private Ijwt ijwt;
    public static final String BEARER = "bearer";
    private UtilisateurService utilisateurService;
    public Map<String,String> generate(String username){
        Utilisateur user=(Utilisateur)this.utilisateurService.loadUserByUsername(username);
        this.disableTokens(user);
        final Map<String, String> jwtMap = new java.util.HashMap<>(this.generateJwt(user));
        RefreshJwt refreshJwt=RefreshJwt
                .builder()
                .valeur(UUID.randomUUID().toString())
                .created(Instant.now())
                .expired(Instant.now().plusMillis(30*60*1000))
                .build();
        final Jwt jwt=Jwt
                .builder()
                .valeur(jwtMap.get(BEARER))
                .deactivated(false)
                .expired(false)
                .refreshJwt(refreshJwt)
                .utilisateur(user).build();
        this.ijwt.save(jwt);
        jwtMap.put(REFRESH_JWT,refreshJwt.toString());
        return jwtMap;
    }

    public String extactUsername(String token) {
        return getClaim(token,Claims::getSubject);
    }

    public boolean isTokenExpired(String token) {
        Date expirationDate= this.getClaim(token,Claims::getExpiration);
        return expirationDate.before(new Date());
    }

    private <T> T getClaim(String token, Function<Claims,T>function) {
        Claims claims = getAllClaims(token);
        return function.apply(claims);
    }

    private Claims getAllClaims(String token) {

        return Jwts
                .parser()
                .verifyWith((SecretKey)genKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }//

    private Map<String, String> generateJwt(Utilisateur user) {
        final long currentTime = System.currentTimeMillis();
        final long expirationTime = currentTime+1000*60;//1000*60*60*24;
        final Map<String, Object> claims = Map.of(
                "nom", user.getNom(),
                "email", user.getEmail(),
                Claims.EXPIRATION,new Date(expirationTime),
                Claims.SUBJECT,user.getEmail()
        );
        final String bearer = Jwts.builder()
                .issuedAt(new Date(currentTime))
                .expiration(new Date(expirationTime))
                .subject(user.getEmail())
                .claims(claims)
                .signWith(genKey())
                .compact();
        return Map.of(BEARER,bearer);
    }

    private Key genKey() {
        final byte[] decode = Decoders.BASE64.decode(Constantes.ENCRIPTION_KEY);
        return Keys.hmacShaKeyFor(decode);
    }


    public Jwt tokenByValue(String token) {
        return this.ijwt.findByValeur(token)
                .orElseThrow(()->new RuntimeException("token not found"));
    }

    public void disableTokens(Utilisateur utilisateur) {
        List<Jwt> jwtList = this.ijwt.findByEmail(utilisateur.getEmail())
                .peek(
                        jwt -> {
                            jwt.setExpired(true);
                            jwt.setDeactivated(true);
                        }
                ).toList();
        this.ijwt.saveAll(jwtList);
    }
    public void deconnexion() {
        Utilisateur utilisateur = (Utilisateur)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

       Jwt jwt= this.ijwt
               .findByValeurValideToken(
                       utilisateur.getEmail(),
                       false,
                       false)
               .orElseThrow(()->new RuntimeException("token not found"));
       jwt.setExpired(true);
       jwt.setDeactivated(true);
       this.ijwt.save(jwt);
    }

    public Jwt findRefreshByValeur(Map<String,String>refreshToken){

        final  Jwt jwt= this.ijwt.findByRefreshJwt(refreshToken.get(REFRESH_JWT))
                .orElseThrow(()->new RuntimeException("Not fount"));
        if(jwt.isExpired() || jwt.getRefreshJwt().getExpired().isBefore(Instant.now())){
            throw  new RuntimeException("Not fount");
        }
         Map<String,String>res=this.generate(jwt.getUtilisateur().getEmail());
        return null;
    }
    // lien util https://crontab.guru/
    //@Scheduled(cron = "@daily")
    @Scheduled(cron = "0 */1 * * * *")
    public void removeUselessJwt(){
        log.info("suppression des tokens {}", Instant.now());
        this.ijwt.deleteAllByExpiredAndDeactivated(true,true);
    }
}
