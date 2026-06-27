package com.loki.tesis.products.dtos.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

public record ProductRequestDto(

        @NotNull(message = "User uuid is mandatory")
        UUID user,

        @NotBlank(message = "Title is mandatory.")
        String title,

        @NotBlank(message = "Description is mandatory.")
        String description,

        @NotNull(message = "Price is mandatory.")
        @Positive(message = "Price must be higher than zero.")
        BigDecimal price,

        @NotNull(message = "Minimum price is mandatory.")
        @Positive(message = "Minimum price must be higher than zero.")
        BigDecimal priceMin,

        @NotNull(message = "You must add at least one category.")
        Set<UUID> categories,

        @NotNull(message = "Stock is mandatory.")
        @PositiveOrZero(message = "Stock must be higher than 0.")
        Integer stock
) {
        @AssertTrue(message = "Minimum price cannot be higher than price.")
        public Boolean isMinimumPriceValid() {
                if(price == null  || priceMin == null)
                        return true;

                return priceMin.compareTo(price) <= 0;
        }
}
