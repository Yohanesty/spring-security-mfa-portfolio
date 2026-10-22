package com.example.mfa.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface DemoUserRepository extends JpaRepository<DemoUser, Long> {
    Optional<DemoUser> findByUsername(String username);

    @Modifying @Transactional
    @Query("update DemoUser u set u.lastAcceptedStep = :step, u.failures = 0 where u.id = :id and u.lastAcceptedStep < :step and u.mfaEnabled = true")
    int acceptStep(@Param("id") Long id, @Param("step") long step);
}