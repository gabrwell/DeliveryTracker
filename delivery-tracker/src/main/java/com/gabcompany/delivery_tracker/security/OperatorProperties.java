package com.gabcompany.delivery_tracker.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.charset.StandardCharsets;

@ConfigurationProperties("app.security.operator")
public record OperatorProperties(String username, String password) {

    public OperatorProperties {
        username = username == null ? "" : username.trim();
        password = password == null ? "" : password;

        if (username.isBlank() != password.isBlank()) {
            throw new IllegalArgumentException(
                    "Configure both OPERATOR_USERNAME and OPERATOR_PASSWORD.");
        }

        if (!password.isBlank() && (password.length() < 12
                || password.getBytes(StandardCharsets.UTF_8).length > 72)) {
            throw new IllegalArgumentException(
                    "OPERATOR_PASSWORD must contain at least 12 characters and at most 72 UTF-8 bytes.");
        }
    }

    public boolean configured() {
        return !username.isBlank();
    }

    @Override
    public String toString() {
        return "OperatorProperties[username=" + username + ", password=[REDACTED]]";
    }
}
