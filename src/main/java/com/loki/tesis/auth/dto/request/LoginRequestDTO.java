package com.loki.tesis.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Length;

public record LoginRequestDTO (

        @NotBlank(message = "El email es incorrecto.")
        @Email(message = "El email es incorrecto.")
        @Size(max = 100, message = "El email es incorrecto.")
        String email,

        @NotBlank(message = "La contraseña es incorrecta.")
        @Size(min = 8, max = 64)
        String password
) {}
