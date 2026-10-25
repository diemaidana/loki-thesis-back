package com.loki.tesis.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record VerifyEmailRequestDTO(
        @NotBlank(message = "El token es obligatorio")
        String token
) {}
