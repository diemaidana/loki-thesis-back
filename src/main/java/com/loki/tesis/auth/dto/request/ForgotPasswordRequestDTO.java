package com.loki.tesis.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordRequestDTO (
        @NotBlank(message = "El email es obligatorio.")
        @Email(message = "El formato de email no es correcto.")
        String email
){}
