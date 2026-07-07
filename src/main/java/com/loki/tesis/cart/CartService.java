package com.loki.tesis.cart;

import com.loki.tesis.cart.repositories.CartItemRepository;
import com.loki.tesis.cart.repositories.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartService {
    private CartRepository cartRepository;
    private CartItemRepository cartItemRepository;
}
