package com.loki.tesis.auth.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Objects;

public record ChangePasswordRequestDTO(
        @NotBlank(message = "Contraseña incorrecta")
        String currentPassword,
        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, max = 64, message = "La contraseña debe tener entre 8 y 64 caracteres")
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).+$",
                message = "La contraseña debe contener al menos una mayuscula, un número y un caracter especial"
        )
        String newPassword,
        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, max = 64, message = "La contraseña debe tener entre 8 y 64 caracteres")
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).+$",
                message = "La contraseña debe contener al menos una mayuscula, un número y un caracter especial"
        )
        String confirmPassword
) {
    @AssertTrue(message = "Las contraseñas deben coincidir.")
    public boolean isPasswordsMatch() {
        return Objects.equals(newPassword, confirmPassword);
    }
    @AssertTrue(message = "Las contraseñas no debe ser la misma")
    public boolean isNewPasswordDifferent() {
        return !Objects.equals(currentPassword, newPassword);
    }
}
