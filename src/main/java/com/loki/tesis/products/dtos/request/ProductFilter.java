package com.loki.tesis.products.dtos.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ProductFilter(
        String title,
        List<UUID> categoryCodes,

        @PositiveOrZero(message = "El precio minimo no puede ser negativo")
        BigDecimal minPrice,

        @PositiveOrZero(message = "El precio maximo no puede ser negativo")
        BigDecimal maxPrice
) {
    @AssertTrue(message = "El precio minimo no puede ser mayor al precio maximo")
    public boolean isPriceRangeValid() {
        if (minPrice == null || maxPrice == null) return true;
        return minPrice.compareTo(maxPrice) <= 0;
    }
}
