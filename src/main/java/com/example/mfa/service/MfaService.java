package com.example.mfa.service;

import com.example.mfa.domain.*;
import com.example.mfa.security.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MfaService {
    private final DemoUserRepository users;
    private final RecoveryCodeRepository recovery;
    private final PasswordEncoder encoder;
    public MfaService(DemoUserRepository users, RecoveryCodeRepository recovery, PasswordEncoder encoder) {
        this.users = users; this.recovery = recovery; this.encoder = encoder;
    }

    public DemoUser password(String username, String password) {
        DemoUser user = users.findByUsername(username).orElseThrow(() -> new SecurityException("Invalid credentials"));
        if (!encoder.matches(password, user.passwordHash)) throw new SecurityException("Invalid credentials");
        return user;
    }
    public DemoUser user(Long id) { return users.findById(id).orElseThrow(() -> new SecurityException("Invalid session")); }

    @Transactional
    public Map<String, String> enrollment(Long id) {
        DemoUser user = user(id);
        if (user.mfaEnabled) throw new SecurityException("Already enrolled");
        if (user.totpSecret == null) { user.totpSecret = Totp.newSecret(); users.save(user); }
        String uri = "otpauth://totp/MFA-Demo:" + URLEncoder.encode(user.username, StandardCharsets.UTF_8)
            + "?secret=" + user.totpSecret + "&issuer=MFA-Demo&algorithm=SHA1&digits=6&period=30";
        return Map.of("secret", user.totpSecret, "otpauthUri", uri);
    }

    @Transactional
    public List<String> confirm(Long id, String code) {
        DemoUser user = user(id);
        if (user.mfaEnabled || user.totpSecret == null) throw new SecurityException("Not enrolling");
        long step = Totp.matchingStep(user.totpSecret, code, Instant.now().getEpochSecond());
        if (step < 0) throw new SecurityException("Invalid OTP");
        user.mfaEnabled = true;
        user.lastAcceptedStep = step;
        user.failures = 0;
        users.save(user);
        recovery.deleteByUserId(id);
        List<String> plain = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            String value = RecoveryCodes.generate();
            plain.add(value);
            recovery.save(new RecoveryCode(id, RecoveryCodes.hash(value)));
        }
        return plain;
    }

    // No enclosing transaction: failure counters must persist on invalid OTP.
    public boolean verify(Long id, String code) {
        DemoUser user = user(id);
        long now = System.currentTimeMillis();
        if (!user.mfaEnabled || user.lockedUntilEpochMs > now) return false;
        long step = Totp.matchingStep(user.totpSecret, code, Instant.now().getEpochSecond());
        if (step >= 0 && users.acceptStep(id, step) == 1) return true;
        recordFailure(id);
        return false;
    }

    @Transactional
    public void recordFailure(Long id) {
        DemoUser user = user(id);
        user.failures++;
        if (user.failures >= 5) {
            user.lockedUntilEpochMs = System.currentTimeMillis() + 300_000;
            user.failures = 0;
        }
        users.save(user);
    }

    @Transactional
    public boolean consumeRecovery(Long id, String code) {
        DemoUser user = user(id);
        if (!user.mfaEnabled || user.lockedUntilEpochMs > System.currentTimeMillis()
                || code == null || !code.matches("[0-9a-f]{32}")) return false;
        Optional<RecoveryCode> found = recovery.findByUserIdAndCodeHash(id, RecoveryCodes.hash(code));
        if (found.isEmpty()) return false;
        recovery.delete(found.get());
        return true;
    }
}