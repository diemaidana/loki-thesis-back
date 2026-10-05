package com.loki.tesis.products.dtos.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ProductSummaryResponseDto(
        UUID productCode,
        String title,
        String description,
        BigDecimal price,
        Integer stock,
        LocalDateTime createdAt,
        String coverImageUrl
) {
}
