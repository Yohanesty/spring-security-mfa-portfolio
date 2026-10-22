package com.example.legacy.mfa.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import com.example.legacy.mfa.security.PendingMfaAuthenticationSuccessHandler;

@Controller
public class MfaPageController {
    @RequestMapping("/login") public String login() { return "login"; }
    @RequestMapping("/mfa/challenge") public String challenge(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(PendingMfaAuthenticationSuccessHandler.USER) == null) return "redirect:/login";
        return "otp";
    }
    @RequestMapping("/home") public String home() { return "home"; }
}
