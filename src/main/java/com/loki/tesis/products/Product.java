package com.loki.tesis.products;

import com.github.f4b6a3.uuid.UuidCreator;
import com.loki.tesis.categories.CategoryEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "products")
@NoArgsConstructor
@AllArgsConstructor
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

    @ManyToMany
    @JoinTable(
            name = "products_categories",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    private Set<CategoryEntity> categories;

    @PrePersist
    public void onCreate() {
        if(this.id == null) {
            id = UuidCreator.getTimeOrderedEpoch();
        }

        createdAt = LocalDateTime.now();
    }
}
