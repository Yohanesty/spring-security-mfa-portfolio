package com.example.mfa.controller;

import com.example.mfa.domain.DemoUser;
import com.example.mfa.service.MfaService;
import jakarta.servlet.http.*;
import java.util.*;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.*;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AuthController {
    private static final String PENDING = "PENDING_USER_ID";
    private static final String SINCE = "PENDING_AT";
    private final MfaService mfa;
    private final HttpSessionSecurityContextRepository contexts;
    public AuthController(MfaService mfa, HttpSessionSecurityContextRepository contexts) {
        this.mfa = mfa; this.contexts = contexts;
    }
    public record Login(String username, String password) {}
    public record Code(String code) {}

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Login input, HttpServletRequest request) {
        try {
            DemoUser user = mfa.password(input.username(), input.password());
            HttpSession old = request.getSession(false);
            if (old != null) old.invalidate();
            HttpSession session = request.getSession(true);
            session.setAttribute(PENDING, user.id);
            session.setAttribute(SINCE, System.currentTimeMillis());
            return ResponseEntity.ok(Map.of("next", user.mfaEnabled ? "/api/otp" : "/api/enrollment"));
        } catch (SecurityException e) { return denied(); }
    }

    @PostMapping("/enrollment")
    public ResponseEntity<?> enrollment(HttpServletRequest request) {
        try { return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(mfa.enrollment(pending(request))); }
        catch (SecurityException e) { return denied(); }
    }

    @PostMapping("/enrollment/confirm")
    public ResponseEntity<?> confirm(@RequestBody Code input, HttpServletRequest request, HttpServletResponse response) {
        try {
            Long id = pending(request);
            List<String> codes = mfa.confirm(id, input.code());
            complete(request, response, id);
            return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(Map.of("authenticated", true, "recoveryCodes", codes));
        } catch (SecurityException e) { return denied(); }
    }

    @PostMapping("/otp")
    public ResponseEntity<?> otp(@RequestBody Code input, HttpServletRequest request, HttpServletResponse response) {
        try {
            Long id = pending(request);
            if (!mfa.verify(id, input.code())) return denied();
            complete(request, response, id);
            return ResponseEntity.ok(Map.of("authenticated", true));
        } catch (SecurityException e) { return denied(); }
    }

    @PostMapping("/recovery")
    public ResponseEntity<?> recovery(@RequestBody Code input, HttpServletRequest request, HttpServletResponse response) {
        try {
            Long id = pending(request);
            if (!mfa.consumeRecovery(id, input.code())) return denied();
            complete(request, response, id);
            return ResponseEntity.ok(Map.of("authenticated", true));
        } catch (SecurityException e) { return denied(); }
    }

    @GetMapping("/me")
    public Map<String, String> me(org.springframework.security.core.Authentication authentication) {
        return Map.of("username", authentication.getName(), "status", "authenticated");
    }

    private Long pending(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || !(session.getAttribute(PENDING) instanceof Long id)
                || !(session.getAttribute(SINCE) instanceof Long at)
                || System.currentTimeMillis() - at > 300_000) throw new SecurityException("Pending session expired");
        return id;
    }

    private void complete(HttpServletRequest request, HttpServletResponse response, Long id) {
        String username = mfa.user(id).username;
        HttpSession session = request.getSession(false);
        if (session != null) session.invalidate();
        request.getSession(true);
        var auth = UsernamePasswordAuthenticationToken.authenticated(username, null,
            List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_USER")));
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
        contexts.saveContext(context, request, response);
    }

    private ResponseEntity<Map<String, String>> denied() {
        return ResponseEntity.status(401).body(Map.of("error", "Authentication failed"));
    }
}