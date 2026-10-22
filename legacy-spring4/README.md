# Legacy MFA reference — Java 8 / Spring 4

**Independently reconstructed sample**, inspired by an enterprise legacy integration pattern. It is NOT the employer's original source with renamed packages.

## Structure
- Java 8, Spring Framework 4.3.x, Spring Security 4.2.x, XML configuration, JSP, WAR
- Package: `com.example.legacy.mfa`
- `PasswordAuthenticationFilter` -> `PendingMfaAuthenticationSuccessHandler` -> pending session
- `OtpAuthenticationFilter` -> `OtpAuthenticationProvider` -> authenticated Spring Security session
- `MfaService`, `TotpService` and `MfaRepository` abstraction
- JSP screens for pending enrollment and OTP verification

## Running and integration status
This module is a **structural reference**, not a production-deployable WAR. The repository implementation is deliberately an in-memory demonstration; database mappings, actual enterprise login handlers, CSRF policy, admin reset, recovery code persistence, account lockout and production deployment descriptors must be completed before deployment.

The parent portfolio project uses Java 21 / Spring Boot 3. This directory has its **own Maven POM** and must be built separately:

```bash
cd legacy-spring4
mvn package
```

A servlet 3.1 compatible container is required. The XML and Java sources are included for code review; successful build and deployment have not been independently verified.

## Differences from modern sample
| Topic | Legacy | Modern |
|---|---|---|
| Runtime | Java 8 | Java 21 |
| Framework | Spring 4, Security 4 | Spring Boot 3, Security 6 |
| Web | WAR, JSP, XML | REST JSON |
| Auth integration | Custom filters and providers | Explicit SecurityContext persistence |
| Persistence | Repository interface + in-memory demo | Spring Data JPA + H2 |

Never reuse the demonstration credentials, secret handling or sample session flow without a security review.
