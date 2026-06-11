package com.loki.tesis.categories.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record CategoryResponseDTO(
        UUID categoryCode,
        String name,
        String description,
        Boolean status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
