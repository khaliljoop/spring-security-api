package com.ikbs.springsecurity.securite;


import com.ikbs.springsecurity.service.UtilisateurService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.http.HttpMethod.POST;

@Configuration
@EnableWebSecurity
public class ConfigurationSecurity {
    private final JwtFilter jwtFilter;
    private final ConfigBCrypPassword configBCrypPassword;
    public ConfigurationSecurity(JwtFilter jwtFilter, ConfigBCrypPassword configBCrypPassword) {
        this.jwtFilter = jwtFilter;
        this.configBCrypPassword = configBCrypPassword;
    }

    @Bean // cest une classe qu'on peut instancier
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        auth->auth
                                .requestMatchers(POST,"/inscription").permitAll()
                                .requestMatchers(POST,"/activation").permitAll()
                                .requestMatchers(POST,"/connexion").permitAll()
                                .requestMatchers(POST,"/reset-password").permitAll()
                                .requestMatchers(POST,"/newpassword").permitAll()
                                .anyRequest().authenticated()
                )
                .sessionManagement(
                        httpSecuritySessionManagementConfigurer -> httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {

        return authenticationConfiguration.getAuthenticationManager();

    }


    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService){
        DaoAuthenticationProvider daoAuthenticationProvider=new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(this.configBCrypPassword.passwordEncoder());
        return  daoAuthenticationProvider;
    }

}
