package com.loki.tesis.cart.dto;

import java.util.UUID;

public record AddCartItemRequestDTO(
        UUID productCode,
        Integer quantity
) {
}
