package com.example.mfa.security;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class IpPrefixAllowlistTest {
    @Test void pipeDelimiterIsLiteral() {
        assertTrue(IpPrefixAllowlist.matches("10.20.30.5", "10.20.30|172.16.4"));
        assertFalse(IpPrefixAllowlist.matches("10.20.300.5", "10.20.30|172.16.4"));
        assertFalse(IpPrefixAllowlist.matches("192.0.2.1", "10.20.30|172.16.4"));
    }
}