package com.loki.tesis.offers.dto;

import com.loki.tesis.offers.OfferStatus;
import com.loki.tesis.products.dtos.response.ProductSummaryResponseDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CreateOfferResponseDTO(
        UUID offerCode,
        OfferStatus status,
        BigDecimal price,
        Integer amount,
        LocalDateTime expiresAt,
        ProductSummaryResponseDto product
) {
}
