package com.loki.tesis.cart;

import com.loki.tesis.cart.dto.CartItemResponseDTO;
import com.loki.tesis.cart.dto.CartResponseDTO;
import com.loki.tesis.cart.entities.Cart;
import com.loki.tesis.cart.entities.CartItem;
import com.loki.tesis.products.Product;
import com.loki.tesis.products.dtos.response.ProductSummaryResponseDto;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-08T22:23:19-0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 25.0.4.1 (Amazon.com Inc.)"
)
@Component
public class CartMapperImpl implements CartMapper {

    @Override
    public CartResponseDTO toCartResponseDTO(Cart cart) {
        if ( cart == null ) {
            return null;
        }

        UUID cartCode = null;
        BigDecimal total = null;
        Integer itemCount = null;
        List<CartItemResponseDTO> cartItems = null;

        cartCode = cart.getCartCode();
        total = cart.getTotal();
        itemCount = cart.getItemCount();
        cartItems = cartItemListToCartItemResponseDTOList( cart.getCartItems() );

        CartResponseDTO cartResponseDTO = new CartResponseDTO( cartCode, total, itemCount, cartItems );

        return cartResponseDTO;
    }

    @Override
    public CartItemResponseDTO toItemResponseDTO(CartItem cartItem) {
        if ( cartItem == null ) {
            return null;
        }

        UUID cartItemCode = null;
        Integer quantity = null;
        BigDecimal unitPrice = null;
        LocalDateTime addedAt = null;
        ProductSummaryResponseDto product = null;

        cartItemCode = cartItem.getCartItemCode();
        quantity = cartItem.getQuantity();
        unitPrice = cartItem.getUnitPrice();
        addedAt = cartItem.getAddedAt();
        product = productToProductSummaryResponseDto( cartItem.getProduct() );

        BigDecimal subtotal = cartItem.getUnitPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));

        CartItemResponseDTO cartItemResponseDTO = new CartItemResponseDTO( cartItemCode, quantity, unitPrice, subtotal, addedAt, product );

        return cartItemResponseDTO;
    }

    protected List<CartItemResponseDTO> cartItemListToCartItemResponseDTOList(List<CartItem> list) {
        if ( list == null ) {
            return null;
        }

        List<CartItemResponseDTO> list1 = new ArrayList<CartItemResponseDTO>( list.size() );
        for ( CartItem cartItem : list ) {
            list1.add( toItemResponseDTO( cartItem ) );
        }

        return list1;
    }

    protected ProductSummaryResponseDto productToProductSummaryResponseDto(Product product) {
        if ( product == null ) {
            return null;
        }

        UUID productCode = null;
        String title = null;
        String description = null;
        BigDecimal price = null;
        Integer stock = null;
        LocalDateTime createdAt = null;

        productCode = product.getProductCode();
        title = product.getTitle();
        description = product.getDescription();
        price = product.getPrice();
        stock = product.getStock();
        createdAt = product.getCreatedAt();

        String coverImageUrl = null;

        ProductSummaryResponseDto productSummaryResponseDto = new ProductSummaryResponseDto( productCode, title, description, price, stock, createdAt, coverImageUrl );

        return productSummaryResponseDto;
    }
}
