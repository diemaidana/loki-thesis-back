package com.loki.tesis.shared.mercadoPago;

import java.math.BigDecimal;

public record MercadoPagoItemDTO(
        String title,
        Integer quantity,
        BigDecimal unitPrice
) {
}
