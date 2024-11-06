package com.ikbs.springsecurity.securite;

import com.ikbs.springsecurity.entite.Jwt;
import com.ikbs.springsecurity.service.UtilisateurService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Service
public class JwtFilter extends OncePerRequestFilter {

    Jwt tokenFromDb;
    private final JwtService jwtService;
    private final UtilisateurService utilisateurService;

    public JwtFilter(JwtService jwtService, UtilisateurService utilisateurService) {
        this.jwtService = jwtService;
        this.utilisateurService = utilisateurService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String username=null;
        String token=null;
        boolean isTokenExpired=true;
        final String authorization=request.getHeader("Authorization");
        if(authorization!=null && authorization.startsWith("Bearer ")) {
            token = authorization.substring(7);
            isTokenExpired = jwtService.isTokenExpired(token);
            tokenFromDb= jwtService.tokenByValue(token);
            username=jwtService.extactUsername(token);
        }
        if (
                !isTokenExpired
                        && tokenFromDb.getUtilisateur().getEmail().equals(username)
                        && SecurityContextHolder.getContext().getAuthentication() == null){
            UserDetails userDetails= utilisateurService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken authenticationToken=new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
        filterChain.doFilter(request, response);
    }
}
