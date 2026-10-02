package com.gabcompany.delivery_tracker.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

class OperatorPropertiesTest {

    @Test
    void shouldNotCreateAnAccountWithoutExplicitCredentials() {
        OperatorProperties properties = new OperatorProperties(null, null);
        SecurityConfig config = new SecurityConfig();

        assertFalse(properties.configured());
        assertThrows(UsernameNotFoundException.class,
                () -> config.userDetailsService(properties, config.passwordEncoder())
                        .loadUserByUsername("user"));
    }

    @Test
    void shouldRejectIncompleteCredentials() {
        assertThrows(IllegalArgumentException.class,
                () -> new OperatorProperties("operator", ""));
        assertThrows(IllegalArgumentException.class,
                () -> new OperatorProperties("", "test-only-password"));
    }

    @Test
    void shouldRejectShortPasswordsAndPasswordsExceedingTheBcryptByteLimit() {
        assertThrows(IllegalArgumentException.class,
                () -> new OperatorProperties("operator", "short"));
        assertThrows(IllegalArgumentException.class,
                () -> new OperatorProperties("operator", "a".repeat(73)));
        assertThrows(IllegalArgumentException.class,
                () -> new OperatorProperties("operator", "\u20ac".repeat(25)));
    }

    @Test
    void shouldStoreOnlyAnEncodedPasswordAndGrantTheOperatorRole() {
        OperatorProperties properties = new OperatorProperties(" operator ", "test-only-password");
        SecurityConfig config = new SecurityConfig();
        PasswordEncoder encoder = config.passwordEncoder();
        UserDetails user = config.userDetailsService(properties, encoder).loadUserByUsername("operator");

        assertNotEquals(properties.password(), user.getPassword());
        assertTrue(encoder.matches(properties.password(), user.getPassword()));
        assertTrue(user.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_OPERATOR")));
        assertFalse(properties.toString().contains(properties.password()));
    }
}
