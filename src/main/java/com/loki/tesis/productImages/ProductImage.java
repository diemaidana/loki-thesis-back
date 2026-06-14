package com.loki.tesis.productImages;

import com.github.f4b6a3.uuid.UuidCreator;
import com.loki.tesis.products.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "product_images")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProductImage {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "image_code", nullable = false, updatable = false)
    private UUID imageCode;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @Column(name = "url", nullable = false)
    private String url;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, updatable = false)
    private Product product;

    @PrePersist
    public void onCreate() {
        imageCode = UuidCreator.getTimeOrderedEpoch();
        createdAt = LocalDateTime.now();
    }
}
