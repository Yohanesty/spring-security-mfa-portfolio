package com.example.legacy.mfa.service;

public class MfaService {
    private MfaRepository repository;
    private TotpService totp;
    public void setRepository(MfaRepository repository) { this.repository = repository; }
    public void setTotp(TotpService totp) { this.totp = totp; }

    public boolean verify(String username, String otp) {
        long now = System.currentTimeMillis();
        if (repository.isLocked(username, now)) return false;
        // Reference-only: repository currently returns demo seed.
        // Production: decrypt encryptedSecret through an external key-managed crypto service.
        String seed = repository.encryptedSecret(username);
        if (seed == null) return false;
        long step = totp.matchStep(seed, otp, now / 1000);
        if (step >= 0 && repository.acceptStepIfNewer(username, step)) return true;
        repository.recordFailure(username, now);
        return false;
    }
}
