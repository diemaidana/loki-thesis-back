package com.loki.tesis.auth.dto.request;

import com.loki.tesis.shared.address.dto.AddressDTO;
import com.loki.tesis.user.enums.SocialNumberType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequestDTO(

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 50, message = "El nombre no puede superar los 50 caracteres")
        String firstName,

        @NotBlank(message = "El apellido es obligatorio")
        @Size(max = 50, message = "El apellido no puede superar los 50 caracteres")
        String lastName,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El formato del email no es valido")
        @Size(max = 100, message = "El email no puede superar los 100 caracteres")
        String email,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, max = 64, message = "La contraseña debe tener entre 8 y 64 caracteres")
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).+$",
                message = "La contraseña debe contener al menos una mayuscula, un número y un caracter especial"
        )
        String password,

        @NotNull(message = "El tipo de documento es obligatorio")
        SocialNumberType documentType,

        @NotBlank(message = "El numero de documento es obligatorio")
        @Pattern(regexp = "^\\d+$", message = "El numero de documento solo puede contener digitos")
        String documentNumber,

        @Size(max = 20, message = "El numero de telefono no puede superar los 20 caracteres")
        @Pattern(
                regexp = "^$|^\\+?[0-9 \\-]{6,20}$",
                message = "El numero de telefono solo puede contener dígitos, espacios, guiones y opcionalmente un '+' al inicio"
        )
        String phoneNumber,

        @Valid
        AddressDTO address

) {

    @AssertTrue(message = "El DNI debe tener 7 u 8 digitos; el CUIT/CUIL debe tener exactamente 11")
    public boolean isLongDocumentValid() {
        if (documentType == null || documentNumber == null) {
            return true; // dejamos que @NotNull / @NotBlank reporten el error real
        }
        int longitud = documentNumber.length();
        return switch (documentType) {
            case DNI -> longitud == 8;
            case CUIT, CUIL -> longitud == 11;
        };
    }
}
