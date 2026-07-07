package com.loki.tesis.cart.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CartResponseDTO(
        UUID cartCode,
        BigDecimal total,
        Integer itemCount,
        List<CartItemResponseDTO> items
) {
}
