# Spring Security MFA Portfolio

Legacy Spring Web 인증 고도화 경험을 TOTP 기반 MFA로 일반화한 **Backend Security Engineering Case Study**입니다. Java 21, Spring Boot 3, Spring Security 6, Spring Data JPA, H2 기반의 독립 예제로 구성했으며 회사 소스·고객정보·내부 URL·실제 Credential은 포함하지 않습니다.

## What is implemented
- BCrypt password verification and an unauthenticated, five-minute pending session
- RFC 6238 TOTP (SHA-1, six digits, 30-second period, ±1 time step)
- Enrollment through a Base32 secret / `otpauth://` URI (Google Authenticator or KeePassXC)
- OTP enrollment confirmation and atomic time-step replay prevention
- Five failed OTP submissions -> five-minute lock
- Five single-use recovery codes, stored as SHA-256 digests (demo only)
- Authenticated Spring Security context created only after MFA completion
- Unit tests for RFC 6238 TOTP and pipe-delimited IP prefixes

## Local run
Requires **JDK 21** and Maven.

```bash
mvn test
mvn spring-boot:run
```

**Demo credentials:** `demo` / `DemoPass123!` (local only).

Use the same cookie jar for each request:

```bash
curl -c cookies.txt -b cookies.txt -H 'Content-Type: application/json' \
  -d '{"username":"demo","password":"DemoPass123!"}' http://localhost:8080/api/login

curl -c cookies.txt -b cookies.txt -X POST http://localhost:8080/api/enrollment
```

The enrollment response includes `secret` and `otpauthUri`. Register the secret in Google Authenticator or KeePassXC. Then:

```bash
curl -c cookies.txt -b cookies.txt -H 'Content-Type: application/json' \
  -d '{"code":"REPLACE_WITH_6_DIGITS"}' http://localhost:8080/api/enrollment/confirm

curl -c cookies.txt -b cookies.txt http://localhost:8080/api/me
```

On later logins, call `POST /api/otp` with `{"code":"123456"}` (replace code) after `POST /api/login`. Alternatively, `POST /api/recovery` accepts one previously issued recovery code.

## Limitations — important
This is **not production-ready**. The demo deliberately disables CSRF protection for simple curl-based requests; browser deployment requires CSRF protection. It uses an in-memory H2 database, fixed demo credentials, plaintext TOTP secrets at rest, no audit trail, no administrator reset, no external key management, no distributed session store, and SHA-256 rather than a dedicated slow recovery-code KDF. It does not include a QR image generator or web UI; use the supplied otpauth URI or manual key.

The source is committed but has **not been compiled or tested in CI in this session**. Unit test results are not claimed. Validate the complete authentication lifecycle and concurrency before relying on this demo.

## Portfolio context
Designed to illustrate modernization of legacy username/password authentication. This public repository contains newly written example code, not a sanitized copy of a proprietary application.


## Related Portfolio

- [Financial Application Architecture Portfolio](https://github.com/Yohanesty/financial-application-architecture-portfolio) — 분산 Application 통합, Legacy Interface Compatibility, Runtime Isolation을 포함한 상위 Architecture 사례
- [JWT + Redis Image Viewer Portfolio](https://github.com/Yohanesty/jwt-redis-image-viewer-portfolio) — JWT와 Redis를 이용한 Backend 접근제어 및 Viewer Session 사례
- [Zabbix Monitoring & Operations Portfolio](https://github.com/Yohanesty/zabbix-monitoring-portfolio) — Application 운영 가시성과 조건부 자동조치 사례
