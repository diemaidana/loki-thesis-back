package com.loki.tesis.products;

import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "products")
@NoArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
public class Product {
    @Id
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    private BigDecimal priceMin;

    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        if(this.id == null) {
            id = UuidCreator.getTimeOrderedEpoch();
        }

        createdAt = LocalDateTime.now();
    }
}
