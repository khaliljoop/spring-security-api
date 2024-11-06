package com.ikbs.springsecurity.securite;


import com.ikbs.springsecurity.constantes.Constantes;
import com.ikbs.springsecurity.entite.Jwt;
import com.ikbs.springsecurity.entite.Utilisateur;
import com.ikbs.springsecurity.service.UtilisateurService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@AllArgsConstructor
@Service
public class JwtService {
    private static final Logger log = LoggerFactory.getLogger(JwtService.class);
    private UtilisateurService utilisateurService;
    public Map<String,String> generate(String username){
        Utilisateur user=(Utilisateur)this.utilisateurService.loadUserByUsername(username);
        final Map<String, String> jwtMap = this.generateJwt(user);
        Jwt.builder().deactivated(false).expired(false).utilisateur(user);
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
        final long expirationTime = currentTime+1000*30*60;//1000*60*60*24;
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
        return Map.of("bearer",bearer);
    }

    private Key genKey() {
        final byte[] decode = Decoders.BASE64.decode(Constantes.ENCRIPTION_KEY);
        return Keys.hmacShaKeyFor(decode);
    }

    
}
