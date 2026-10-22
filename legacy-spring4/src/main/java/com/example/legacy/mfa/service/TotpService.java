package com.example.legacy.mfa.service;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/** RFC 6238 HMAC-SHA1, six digits, 30-second period. Java 8 compatible. */
public class TotpService {
    private static final String BASE32 = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";
    public long matchStep(String secret, String otp, long epochSeconds) {
        if (otp == null || !otp.matches("[0-9]{6}")) return -1L;
        long now = epochSeconds / 30L;
        for (long step = now - 1; step <= now + 1; step++) {
            if (step >= 0 && MessageDigest.isEqual(code(secret, step).getBytes(StandardCharsets.US_ASCII),
                    otp.getBytes(StandardCharsets.US_ASCII))) return step;
        }
        return -1L;
    }
    public String code(String base32, long step) {
        try {
            byte[] secret = decode(base32);
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(secret, "HmacSHA1"));
            byte[] hash = mac.doFinal(ByteBuffer.allocate(8).putLong(step).array());
            int offset = hash[hash.length - 1] & 15;
            int value = ((hash[offset] & 127) << 24) | ((hash[offset + 1] & 255) << 16)
                | ((hash[offset + 2] & 255) << 8) | (hash[offset + 3] & 255);
            return String.format(java.util.Locale.ROOT, "%06d", value % 1000000);
        } catch (Exception e) { throw new IllegalStateException("TOTP failure", e); }
    }
    private byte[] decode(String encoded) {
        int bits = 0, buffer = 0, count = 0;
        byte[] result = new byte[encoded.length() * 5 / 8];
        for (char c : encoded.toUpperCase(java.util.Locale.ROOT).toCharArray()) {
            int value = BASE32.indexOf(c);
            if (value < 0) throw new IllegalArgumentException("Invalid Base32");
            buffer = (buffer << 5) | value; bits += 5;
            if (bits >= 8) { bits -= 8; result[count++] = (byte)(buffer >>> bits); }
        }
        return java.util.Arrays.copyOf(result, count);
    }
}
