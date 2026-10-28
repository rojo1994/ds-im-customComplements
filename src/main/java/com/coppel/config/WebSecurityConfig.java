package com.coppel.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * filtro principal de seguridad
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig
{
    /**
     * Configuración del filtro de seguridad
     * @return SecurityFilterChain
     */
    @Bean
    SecurityFilterChain filterchain(HttpSecurity http) 
    throws Exception 
    {
        return http
            .cors(withDefaults())
            .csrf(conf -> conf.disable())
            .formLogin(conf -> conf.disable())
            .httpBasic(conf -> conf.disable())
            .headers(header -> header.contentSecurityPolicy(withDefaults()))
            .build();
    }

    @Bean
    UserDetailsService userDetailsService() {
        return new InMemoryUserDetailsManager();
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}