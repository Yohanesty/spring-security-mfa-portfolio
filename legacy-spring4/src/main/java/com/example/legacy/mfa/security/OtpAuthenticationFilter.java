package com.example.legacy.mfa.security;

import javax.servlet.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

public class OtpAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    public OtpAuthenticationFilter() {
        super(new AntPathRequestMatcher("/mfa/verify", "POST"));
    }
    @Override public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        if (session == null || !(session.getAttribute(PendingMfaAuthenticationSuccessHandler.USER) instanceof String)
                || !(session.getAttribute(PendingMfaAuthenticationSuccessHandler.AT) instanceof Long)) {
            throw new BadCredentialsException("No pending authentication");
        }
        long at = (Long) session.getAttribute(PendingMfaAuthenticationSuccessHandler.AT);
        if (System.currentTimeMillis() - at > 300000L) {
            session.invalidate();
            throw new CredentialsExpiredException("Pending authentication expired");
        }
        String username = (String) session.getAttribute(PendingMfaAuthenticationSuccessHandler.USER);
        return getAuthenticationManager().authenticate(new OtpAuthenticationToken(username, request.getParameter("otp")));
    }
}
