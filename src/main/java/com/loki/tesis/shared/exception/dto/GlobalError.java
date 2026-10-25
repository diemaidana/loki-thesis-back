package com.loki.tesis.shared.exception.dto;

// Representa un error de validacion general completo, no de un campo puntual.
// Ej: valida la coherencia entre tipoDcoumento y numeroDocumento

public record GlobalError(
        String code,
        String message
) {
}

// Representa el formato que se va a tomar cuando se muestre el error Global.