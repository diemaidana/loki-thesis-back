package com.loki.tesis.cart.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record AddCartItemRequestDTO(
        @NotNull(message = "Product code is mandatory.")
        UUID productCode,

        @NotNull(message = "Quantity is mandatory.")
        @Positive(message = "Quantity must be at least one.")
        Integer quantity
) {
}
