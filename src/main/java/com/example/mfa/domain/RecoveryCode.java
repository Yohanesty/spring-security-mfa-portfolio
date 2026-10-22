package com.example.mfa.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "recovery_codes")
public class RecoveryCode {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public Long userId;
    @Column(nullable = false)
    public String codeHash;
    protected RecoveryCode() {}
    public RecoveryCode(Long userId, String codeHash) { this.userId = userId; this.codeHash = codeHash; }
}