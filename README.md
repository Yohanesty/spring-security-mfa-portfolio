# Spring Security MFA Portfolio

An independently written educational implementation of password + TOTP authentication. **No employer source code or internal system details are included.**

## Technology
- Java 21 / Spring Boot 3 / Spring Security 6
- Maven, H2 in-memory database
- RFC 6238-compatible TOTP, Google Authenticator or KeePassXC

## Status
Initial project setup. Additional code is being added in subsequent commits. This is a demonstration, **not production-ready authentication software**.

## Local use
`mvn test` and `mvn spring-boot:run` once the implementation is complete.

## Security
Never commit actual OTP seeds, customer data, corporate addresses, credentials, or screenshots of internal systems.
