package com.example.legacy.mfa.service;

/** Replace this abstraction with an Oracle/MyBatis mapper in enterprise integrations. */
public interface MfaRepository {
    String encryptedSecret(String username);
    boolean acceptStepIfNewer(String username, long step);
    boolean isLocked(String username, long nowMillis);
    void recordFailure(String username, long nowMillis);
}
