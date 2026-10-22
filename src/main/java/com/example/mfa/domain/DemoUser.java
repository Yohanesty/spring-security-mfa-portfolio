package com.example.mfa.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "demo_users")
public class DemoUser {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    @Column(nullable = false, unique = true)
    public String username;
    @Column(nullable = false)
    public String passwordHash;
    public String totpSecret;
    public boolean mfaEnabled;
    public long lastAcceptedStep = -1;
    public int failures;
    public long lockedUntilEpochMs;
    protected DemoUser() {}
    public DemoUser(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
    }
}