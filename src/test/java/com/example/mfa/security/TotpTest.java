package com.example.mfa.security;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TotpTest {
    @Test void generatedSecretWorks() {
        String secret = Totp.newSecret();
        assertEquals(32, secret.length());
        long time = 1_700_000_000L;
        String code = Totp.atStep(secret, time / 30);
        assertEquals(time / 30, Totp.matchingStep(secret, code, time));
        assertEquals(-1, Totp.matchingStep(secret, "nototp", time));
    }

    @Test void knownRfc6238Sha1Vector() {
        String secret = "GEZDGNBVGY3TQOJQGEZDGNBVGY3TQOJQ";
        assertEquals("287082", Totp.atStep(secret, 59 / 30));
    }
}