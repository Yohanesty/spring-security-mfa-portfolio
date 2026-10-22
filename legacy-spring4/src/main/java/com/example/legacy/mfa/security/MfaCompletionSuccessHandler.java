package com.example.legacy.mfa.security;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

/** Session fixation mitigation and redirect after successful OTP. */
public class MfaCompletionSuccessHandler implements AuthenticationSuccessHandler {
    @Override public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(PendingMfaAuthenticationSuccessHandler.USER);
            session.removeAttribute(PendingMfaAuthenticationSuccessHandler.AT);
            request.changeSessionId();
        }
        response.sendRedirect(request.getContextPath() + "/home");
    }
}
