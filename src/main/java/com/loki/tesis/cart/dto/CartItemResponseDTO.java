package com.loki.tesis.cart.dto;

import com.loki.tesis.products.dtos.response.ProductSummaryResponseDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CartItemResponseDTO(
        UUID cartItemCode,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal,
        LocalDateTime addedAt,
        ProductSummaryResponseDto product
) {
}
