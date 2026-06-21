package com.loki.tesis.auth.dto.response;

public record LoginResponseDTO (
        String token,
        String expiresAt,
        AccountResponseDTO account
) {}