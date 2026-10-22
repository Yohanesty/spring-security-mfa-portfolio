package com.example.mfa.security;

import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }
    @Bean HttpSessionSecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }
    @Bean SecurityFilterChain filterChain(HttpSecurity http, HttpSessionSecurityContextRepository repository) throws Exception {
        return http
            // Curl-based JSON demo only. Browser deployments MUST enable CSRF protection.
            .csrf(csrf -> csrf.disable())
            .securityContext(ctx -> ctx.securityContextRepository(repository).requireExplicitSave(true))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/login", "/api/enrollment", "/api/enrollment/confirm", "/api/otp", "/api/recovery").permitAll()
                .anyRequest().authenticated())
            .formLogin(login -> login.disable())
            .httpBasic(basic -> basic.disable())
            .build();
    }
}