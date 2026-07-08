package com.loki.tesis.cart.entities;

import com.github.f4b6a3.uuid.UuidCreator;
import com.loki.tesis.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    private List<CartItem> cartItems;

    @PrePersist
    public void onCreate() {
        cartCode = UuidCreator.getTimeOrderedEpoch();
        createdAt = LocalDateTime.now();
    }

    public Optional<CartItem> containsItem(Long productId) {
        return cartItems.stream()
                .filter(c -> c.getProduct().getId().equals(productId))
                .findFirst();
    }

    public void addCartItem(CartItem cartItem) {
        cartItems.add(cartItem);
    }

    public BigDecimal getTotal() {
        return cartItems.stream()
                .map(cartItem -> cartItem.getUnitPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Integer getItemCount() {
        return cartItems.size();
    }
}
