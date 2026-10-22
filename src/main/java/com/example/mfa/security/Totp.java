package com.example.mfa.security;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public final class Totp {
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";
    private Totp() {}

    public static String newSecret() {
        byte[] bytes = new byte[20];
        new SecureRandom().nextBytes(bytes);
        StringBuilder result = new StringBuilder();
        int buffer = 0, bits = 0;
        for (byte b : bytes) {
            buffer = (buffer << 8) | (b & 255);
            bits += 8;
            while (bits >= 5) {
                bits -= 5;
                result.append(ALPHABET.charAt((buffer >>> bits) & 31));
            }
        }
        if (bits > 0) result.append(ALPHABET.charAt((buffer << (5 - bits)) & 31));
        return result.toString();
    }

    public static byte[] decode(String base32) {
        int buffer = 0, bits = 0;
        byte[] out = new byte[base32.length() * 5 / 8];
        int index = 0;
        for (char c : base32.toUpperCase(java.util.Locale.ROOT).toCharArray()) {
            int value = ALPHABET.indexOf(c);
            if (value < 0) throw new IllegalArgumentException("Invalid Base32");
            buffer = (buffer << 5) | value;
            bits += 5;
            if (bits >= 8) {
                bits -= 8;
                out[index++] = (byte)(buffer >>> bits);
            }
        }
        return java.util.Arrays.copyOf(out, index);
    }

    public static String atStep(String base32, long step) {
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(decode(base32), "HmacSHA1"));
            byte[] h = mac.doFinal(ByteBuffer.allocate(8).putLong(step).array());
            int offset = h[h.length - 1] & 15;
            int value = ((h[offset] & 127) << 24) | ((h[offset + 1] & 255) << 16)
                | ((h[offset + 2] & 255) << 8) | (h[offset + 3] & 255);
            return String.format(java.util.Locale.ROOT, "%06d", value % 1_000_000);
        } catch (Exception e) { throw new IllegalStateException("TOTP calculation failed", e); }
    }

    public static long matchingStep(String secret, String code, long epochSeconds) {
        if (code == null || !code.matches("[0-9]{6}")) return -1;
        long now = epochSeconds / 30;
        for (long step = now - 1; step <= now + 1; step++) {
            if (step >= 0 && java.security.MessageDigest.isEqual(
                    atStep(secret, step).getBytes(java.nio.charset.StandardCharsets.US_ASCII),
                    code.getBytes(java.nio.charset.StandardCharsets.US_ASCII))) return step;
        }
        return -1;
    }
}