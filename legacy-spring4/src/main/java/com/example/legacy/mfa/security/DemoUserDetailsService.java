package com.example.legacy.mfa.security;

import java.util.Collections;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Local demonstration identity only. Replace with existing corporate
 * UserDetailsService; never commit actual user passwords or hashes.
 */
public class DemoUserDetailsService implements UserDetailsService {
    private final String hash = new BCryptPasswordEncoder().encode("DemoPass123!");
    @Override public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (!"demo".equals(username)) throw new UsernameNotFoundException("Unknown user");
        return new User("demo", hash, Collections.singletonList(new SimpleGrantedAuthority("ROLE_PRE_MFA")));
    }
}