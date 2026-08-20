package com.loki.tesis.cart.entities;

import com.github.f4b6a3.uuid.UuidCreator;
import com.loki.tesis.products.Product;
import com.loki.tesis.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Entity
@Table(name = "carts")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Cart {
    @Id
    @SequenceGenerator(name = "carts_seq", sequenceName = "carts_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "carts_seq")
    private Long id;

    @Column(nullable = false)
    private UUID cartCode;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems = new ArrayList<>();

    @PrePersist
    public void onCreate() {
        cartCode = UuidCreator.getTimeOrderedEpoch();
        createdAt = LocalDateTime.now();
    }

    private Boolean containsItem(UUID productCode) {
        return cartItems.stream()
                .anyMatch(c -> c.getProduct().getProductCode().equals(productCode));
    }

    public BigDecimal getTotal() {
        return cartItems.stream()
                .map(cartItem -> cartItem.getUnitPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Integer getItemCount() {
        return cartItems.size();
    }

    private void addAmount(UUID productCode, Integer quantity) {
        cartItems.stream()
                .filter(c -> c.getProduct().getProductCode().equals(productCode))
                .findFirst()
                .ifPresent(c -> c.addQuantity(quantity));
    }

    public void addItem(Product product, Integer quantity) {
        if (containsItem(product.getProductCode())) {
            addAmount(product.getProductCode(), quantity);
        } else {
            CartItem cartItem = new CartItem();
            cartItem.setCart(this);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItem.setUnitPrice(product.getPrice());
            cartItems.add(cartItem);
        }
    }

    public Integer getProductQuantity(Product product) {
        return cartItems.stream()
                .filter(c -> c.getProduct().equals(product))
                .map(CartItem::getQuantity)
                .findFirst()
                .orElse(0);
    }

    public void updateCartItemQuantity(UUID cartItemCode, Integer quantity) {
        cartItems.stream()
                .filter(c -> c.getCartItemCode().equals(cartItemCode))
                .findFirst();
    }
}
