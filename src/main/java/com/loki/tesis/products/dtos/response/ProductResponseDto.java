package com.loki.tesis.products.dtos.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ProductResponseDto(
        UUID productCode,
        String title,
        String description,
        BigDecimal price,
        LocalDateTime createdAt
) {
}
