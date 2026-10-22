package com.example.legacy.mfa.service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/** Non-persistent sample only. Does not represent production encryption or account storage. */
public class InMemoryMfaRepository implements MfaRepository {
    private final ConcurrentMap<String, State> users = new ConcurrentHashMap<String, State>();
    private static final class State {
        String seed;
        long lastStep = -1;
        int failures;
        long lockedUntil;
        State(String seed) { this.seed = seed; }
    }
    public void setDemoSeed(String username, String seed) { users.put(username, new State(seed)); }
    @Override public String encryptedSecret(String username) {
        State s = users.get(username); return s == null ? null : s.seed;
    }
    @Override public boolean acceptStepIfNewer(String username, long step) {
        State s = users.get(username);
        if (s == null) return false;
        synchronized (s) {
            if (step <= s.lastStep) return false;
            s.lastStep = step; s.failures = 0; return true;
        }
    }
    @Override public boolean isLocked(String username, long now) {
        State s = users.get(username);
        return s == null || s.lockedUntil > now;
    }
    @Override public void recordFailure(String username, long now) {
        State s = users.get(username);
        if (s == null) return;
        synchronized (s) {
            if (++s.failures >= 5) { s.lockedUntil = now + 300000L; s.failures = 0; }
        }
    }
}
