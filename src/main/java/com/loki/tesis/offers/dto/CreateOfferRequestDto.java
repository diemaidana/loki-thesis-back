package com.loki.tesis.offers.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateOfferRequestDto(
        @NotNull(message = "Product public ID is mandatory.")
        UUID productCode,

        @NotNull(message = "Buyer public ID is mandatory")
        UUID buyerCode,

        @NotNull(message = "Price offered is mandatory.")
        @Positive(message = "Price offered must be positive.")
        BigDecimal price,

        @NotNull(message = "Amount is mandatory.")
        @Positive(message = "Amount must be positive.")
        Integer amount
) {
}
