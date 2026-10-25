package com.loki.tesis.productImages;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProductImageResponseDTO(
        UUID imageCode,
        Integer displayOrder,
        String url,
        LocalDateTime createdAt
) {
}
