package com.example.importtomo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeHttpRequests(registry -> {
                    // Allow unauthenticated access to the login page and static resources
                    registry.requestMatchers("/css/**", "/js/**", "/images/**").permitAll();
                    registry.requestMatchers("/").permitAll(); // Allow /connect as the OAuth2 login endpoint
                    registry.anyRequest().authenticated(); // Require authentication for all other requests
                })
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/")
                        .defaultSuccessUrl("/dashboard", true) // Redirect to /logged-in on successful login
                        .failureUrl("/login-error") // Add a custom failure URL to handle errors
                );

        return http.build();
    }


}