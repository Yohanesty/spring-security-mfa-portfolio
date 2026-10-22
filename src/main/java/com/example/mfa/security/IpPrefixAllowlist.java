package com.example.mfa.security;

public final class IpPrefixAllowlist {
    private IpPrefixAllowlist() {}
    public static boolean matches(String ip, String prefixes) {
        if (ip == null || prefixes == null) return false;
        for (String value : prefixes.split("\\|")) {
            String prefix = value.trim();
            if (!prefix.isEmpty() && (ip.equals(prefix) || ip.startsWith(prefix.endsWith(".") ? prefix : prefix + "."))) {
                return true;
            }
        }
        return false;
    }
}