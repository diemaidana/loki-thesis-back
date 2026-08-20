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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartService {
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final CartMapper cartMapper;

    @Transactional
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

        checkStock(product.getStock(), request.quantity() + cart.getProductQuantity(product));

        cart.addItem(product, request.quantity());

        return cartMapper.toCartResponseDTO(cartRepository.save(cart));
    }

    private void checkStock(Integer productStock, int quantityRequested) {
        if(productStock < quantityRequested)
            throw new IllegalArgumentException("Stock insufficient.");
    }

    public CartResponseDTO getCart(UUID userCode) {
        User user = userRepository.findByUuid(userCode)
                .orElseThrow(() -> new EntityNotFoundException("User not found."));
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Cart not found."));

        return cartMapper.toCartResponseDTO(cart);
    }

    public CartResponseDTO updateQuantity(UUID userCode, UUID cartItemCode, Integer quantity) {
        User user = userRepository.findByUuid(userCode)
                .orElseThrow(() -> new EntityNotFoundException("User not found."));
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Cart not found."));

        cart.updateCartItemQuantity(cartItemCode, quantity);
    }
}
