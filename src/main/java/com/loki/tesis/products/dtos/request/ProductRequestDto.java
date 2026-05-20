package com.loki.tesis.products.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ProductRequestDto(
        @NotBlank(message = "El titulo es obligatorio.")
        String title,

        @NotBlank(message = "La descripcion es obligatoria.")
        String description,

        @NotNull(message = "El precio es obligatorio.")
        BigDecimal price,

        BigDecimal priceMin
) {
}
