package com.loki.tesis.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record VerifyEmailRequestDTO(
        @NotBlank(message = "El token es obligatorio")
        String token
) {}
