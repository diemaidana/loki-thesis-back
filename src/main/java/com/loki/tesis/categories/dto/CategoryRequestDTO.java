package com.loki.tesis.categories.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoryRequestDTO(
        @NotBlank(message = "Name is mandatory.")
        String name,

        @NotBlank(message = "Description is mandatory.")
        String description
) {
}
