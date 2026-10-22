package com.example.mfa;

import com.example.mfa.domain.DemoUser;
import com.example.mfa.domain.DemoUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class MfaApplication {
    public static void main(String[] args) { SpringApplication.run(MfaApplication.class, args); }

    @Bean CommandLineRunner seed(DemoUserRepository users, PasswordEncoder passwords) {
        return args -> {
            if (users.findByUsername("demo").isEmpty()) {
                users.save(new DemoUser("demo", passwords.encode("DemoPass123!")));
            }
        };
    }
}