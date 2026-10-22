package com.example.legacy.mfa.security;

import java.util.Collections;
import org.springframework.security.authentication.AbstractAuthenticationToken;

public class OtpAuthenticationToken extends AbstractAuthenticationToken {
    private static final long serialVersionUID = 1L;
    private final String username;
    private final String otp;
    public OtpAuthenticationToken(String username, String otp) {
        super(Collections.emptyList());
        this.username = username; this.otp = otp;
        setAuthenticated(false);
    }
    @Override public Object getCredentials() { return otp; }
    @Override public Object getPrincipal() { return username; }
}
