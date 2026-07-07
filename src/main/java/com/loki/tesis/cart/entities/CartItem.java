package com.loki.tesis.cart.entities;

import com.github.f4b6a3.uuid.UuidCreator;
import com.loki.tesis.products.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "cart_items")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CartItem {

    @Id
    @SequenceGenerator(name = "cart_item_seq", sequenceName = "cart_item_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "carts_item_seq")
    private Long id;

    @Column(nullable = false, updatable = false)
    private UUID cartItemCode;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private BigDecimal unitPrice;

    @Column(nullable = false, updatable = false)
    private LocalDateTime addedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @PrePersist
    public void onCreate() {
        cartItemCode = UuidCreator.getTimeOrderedEpoch();
        addedAt = LocalDateTime.now();
    }
}
