package com.loki.tesis.shared.security.dto;

import java.time.Instant;

public record IssuedToken(
        String token,
        Instant expiresAt
) {}
