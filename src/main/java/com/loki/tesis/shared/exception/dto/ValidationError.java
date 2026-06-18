package com.loki.tesis.shared.exception.dto;

// Representa un error de validacion sobre un campo específico.
public record ValidationError(
        String field,           // nombre del campo.
        Object rejectedValue,   // que es lo que envio y se rechazo.
        String code,            // codigo de lo que fallo. Ej: Email etc, etc.
        String message          // mensaje del error
) {
}
