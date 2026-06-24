package com.loki.tesis.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO (

        @NotBlank(message = "El email es incorrecto.")
        @Email(message = "El email es incorrecto.")
        String email,

        @NotBlank(message = "La contraseña es incorrecta.")
        String password
) {}
