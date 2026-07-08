package com.loki.tesis.cart;

import com.loki.tesis.cart.dto.AddCartItemRequestDTO;
import com.loki.tesis.cart.dto.CartItemResponseDTO;
import com.loki.tesis.cart.dto.CartResponseDTO;
import com.loki.tesis.cart.entities.Cart;
import com.loki.tesis.cart.entities.CartItem;
import com.loki.tesis.cart.repositories.CartItemRepository;
import com.loki.tesis.cart.repositories.CartRepository;
import com.loki.tesis.products.Product;
import com.loki.tesis.products.ProductRepository;
import com.loki.tesis.products.ProductStatus;
import com.loki.tesis.user.entity.User;
import com.loki.tesis.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartService {
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    public CartResponseDTO addItem(UUID userCode, AddCartItemRequestDTO request) {
        User buyer = userRepository.findByUuid(userCode)
                .orElseThrow(() -> new EntityNotFoundException("User not found."));

        Cart cart = cartRepository.findByUserId(buyer.getId())
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(buyer);
                    return newCart;
                });

        Product product = productRepository.findByProductCodeAndStatus(request.productCode(), ProductStatus.PUBLISHED)
                .orElseThrow(() -> new EntityNotFoundException("Product not found."));

        checkStock(product.getStock(), request.quantity());

        Optional<CartItem> cartItemOptional = cartItemRepository.findByProductId(product.getId());

        if(cartItemOptional.isPresent()) {
            CartItem cartItem = cartItemOptional.get();
            //Integer actualQuantity = cartItem.getQuantity(); //cart.getCartItemQuantity(product.getId());
            checkStock(product.getStock(), cartItem.getQuantity() + request.quantity());
            cartItem.updateQuantity(request.quantity());
            //cart.updateCartItem(cartItem.get(), request.quantity());
        } else {
            CartItem cartItem = createCartItem(product, request.quantity());
            cartItem.setCart(cart);
            cart.addCartItem(cartItem);
        }

        Cart saved = cartRepository.save(cart);

        return new CartResponseDTO(
                saved.getCartCode(),
                saved.getTotal(),
                saved.getItemCount(),
                createCartItemsResponse(saved.getCartItems())
        );
    }

    private CartItem createCartItem(Product product, Integer quantity) {
        CartItem cartItem =  new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(quantity);
        cartItem.setUnitPrice(product.getPrice());

        return cartItem;
    }

    private void checkStock(Integer productStock, int quantityRequested) {
        if(productStock < quantityRequested)
            throw new IllegalArgumentException("Stock insufficient.");
    }
}
