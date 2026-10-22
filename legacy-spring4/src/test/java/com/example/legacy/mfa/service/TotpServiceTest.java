package com.example.legacy.mfa.service;

import org.junit.Test;
import static org.junit.Assert.*;

public class TotpServiceTest {
    @Test public void rfc6238SixDigitVector() {
        TotpService totp = new TotpService();
        String secret = "GEZDGNBVGY3TQOJQGEZDGNBVGY3TQOJQ";
        assertEquals("287082", totp.code(secret, 59L / 30L));
        assertEquals(1L, totp.matchStep(secret, "287082", 59L));
    }
    @Test public void rejectsMalformedOtp() {
        assertEquals(-1L, new TotpService().matchStep("GEZDGNBVGY3TQOJQGEZDGNBVGY3TQOJQ", "123", 59L));
    }
}
