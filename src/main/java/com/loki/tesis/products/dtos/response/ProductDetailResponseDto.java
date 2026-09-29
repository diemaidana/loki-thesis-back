package com.loki.tesis.products.dtos.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ProductDetailResponseDto(
        UUID productCode,
        String title,
        String description,
        BigDecimal price,
        Integer stock,
        LocalDateTime createdAt,
        List<String> images
) {
}
