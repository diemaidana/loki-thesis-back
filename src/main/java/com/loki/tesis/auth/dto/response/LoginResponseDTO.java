package com.loki.tesis.auth.dto.response;

import java.time.Instant;

public record LoginResponseDTO (
        String token,
        Instant expiresAt,
        AccountResponseDTO account
) {}