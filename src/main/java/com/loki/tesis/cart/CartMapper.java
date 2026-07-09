package com.loki.tesis.cart;

import com.loki.tesis.cart.dto.CartItemResponseDTO;
import com.loki.tesis.cart.dto.CartResponseDTO;
import com.loki.tesis.cart.entities.Cart;
import com.loki.tesis.cart.entities.CartItem;
import com.loki.tesis.products.ProductMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

@Mapper(
        componentModel = "spring",
        uses = {ProductMapper.class},
        imports = {BigDecimal.class}
)
public interface CartMapper {

    CartResponseDTO toCartResponseDTO(Cart cart);

    @Mapping(
            target = "subtotal",
            expression = "java(cartItem.getUnitPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())))")
    CartItemResponseDTO toItemResponseDTO(CartItem cartItem);

}
