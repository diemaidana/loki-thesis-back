package com.loki.tesis.cart;

import com.loki.tesis.cart.dto.AddCartItemRequestDTO;
import com.loki.tesis.cart.dto.CartResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    // DELETE USER CODE ONCE SECURITY IS IMPLEMENTED
    @PostMapping("/items/{userCode}")
    public ResponseEntity<CartResponseDTO> addItems(@Valid @RequestBody AddCartItemRequestDTO request,
                                                    @PathVariable UUID userCode) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cartService.addItem(userCode, request));
    }

    @GetMapping("/{userCode}")
    public ResponseEntity<CartResponseDTO> getCart(@PathVariable UUID userCode) {
        return ResponseEntity.ok(cartService.getCart(userCode));
    }

    // DELETE USERCODE ONCE SECURITY IS IMPLEMENTED
    @PatchMapping("/{userCode}/items/{cartItemCode}")
    public ResponseEntity<CartResponseDTO> updateQuantity(@PathVariable UUID userCode,
                                                          @PathVariable UUID cartItemCode,
                                                          @RequestBody Integer quantity) {
        return ResponseEntity.ok(cartService.updateQuantity(userCode, cartItemCode, quantity));
    }

    @DeleteMapping("/{userCode}/items/{cartItemCode}")
    public ResponseEntity<Void> deleteCartItem(@PathVariable UUID userCode,
                                                          @PathVariable UUID cartItemCode) {
        cartService.deleteCartItem(userCode, cartItemCode);
        return ResponseEntity.noContent().build();
    }
}
