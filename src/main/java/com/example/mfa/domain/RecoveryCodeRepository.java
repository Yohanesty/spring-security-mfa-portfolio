package com.example.mfa.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecoveryCodeRepository extends JpaRepository<RecoveryCode, Long> {
    Optional<RecoveryCode> findByUserIdAndCodeHash(Long userId, String codeHash);
    void deleteByUserId(Long userId);
}