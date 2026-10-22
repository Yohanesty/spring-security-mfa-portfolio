package com.example.legacy.mfa.security;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

/** The first factor must NOT create an authenticated application session. */
public class PendingMfaAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    public static final String USER = "MFA_PENDING_USER";
    public static final String AT = "MFA_PENDING_AT";
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        HttpSession old = request.getSession(false);
        if (old != null) old.invalidate();
        HttpSession session = request.getSession(true);
        session.setAttribute(USER, authentication.getName());
        session.setAttribute(AT, System.currentTimeMillis());
        // First-factor Authentication must not be retained in the SecurityContext.
        org.springframework.security.core.context.SecurityContextHolder.clearContext();
        response.sendRedirect(request.getContextPath() + "/mfa/challenge");
    }
}
