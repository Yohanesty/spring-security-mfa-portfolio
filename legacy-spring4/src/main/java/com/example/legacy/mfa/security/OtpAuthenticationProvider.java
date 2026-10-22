package com.example.legacy.mfa.security;

import java.util.Collections;
import com.example.legacy.mfa.service.MfaService;
import org.springframework.security.authentication.*;
import org.springframework.security.core.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class OtpAuthenticationProvider implements AuthenticationProvider {
    private MfaService service;
    public void setService(MfaService service) { this.service = service; }
    @Override public Authentication authenticate(Authentication request) throws AuthenticationException {
        OtpAuthenticationToken token = (OtpAuthenticationToken) request;
        String username = (String) token.getPrincipal();
        if (!service.verify(username, (String) token.getCredentials()))
            throw new BadCredentialsException("Invalid MFA verification");
        return new UsernamePasswordAuthenticationToken(username, null,
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
    }
    @Override public boolean supports(Class<?> authentication) {
        return OtpAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
