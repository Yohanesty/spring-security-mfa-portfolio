# Spring Security XML integration notes

This sample intentionally separates first and second factors:

1. `UsernamePasswordAuthenticationFilter` validates ID/password via `DaoAuthenticationProvider`.
2. `PendingMfaAuthenticationSuccessHandler` stores only pending username/time and clears the first-factor context.
3. `OtpAuthenticationFilter` reads pending state and requests `OtpAuthenticationToken`.
4. `OtpAuthenticationProvider` validates TOTP and returns a fully authenticated token.
5. `MfaCompletionSuccessHandler` removes pending state and redirects to the protected page.

## Before deploying
- Wire filters/providers into a Spring Security 4 XML `<http>` chain and verify ordering with integration tests.
- Add proper password user store (the demo account is not an enterprise account).
- Replace the in-memory repository with Oracle/MyBatis mappings and encrypt TOTP secrets.
- Add enrollment QR/manual key flow, recovery codes, administrator reset, lockout auditing and server-side CSRF protection.
- Check session fixation and pending-user access under every route, including alternate entry points.
- Test concurrent replay prevention using DB atomic updates.

The architecture reflects the previously discussed enterprise pattern, but the original deployed configuration and actual source files were not available.
