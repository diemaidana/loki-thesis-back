package com.loki.tesis.products.entities;

import com.github.f4b6a3.uuid.UuidCreator;
import com.loki.tesis.categories.CategoryEntity;
import com.loki.tesis.products.ProductStatus;
import com.loki.tesis.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(updatable = false, nullable = false)
    private UUID productCode;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private BigDecimal priceMin;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "status", nullable = false)
    private ProductStatus status;

    @Column(name = "stock", nullable = false)
    private Integer stock;

    @ManyToMany
    @JoinTable(
            name = "products_categories",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    private Set<CategoryEntity> categories;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false, updatable = false)
    private User seller;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> images = new ArrayList<>();

    @PrePersist
    public void onCreate() {
        productCode = UuidCreator.getTimeOrderedEpoch();
        status = ProductStatus.UNPUBLISHED;
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void publish() {
        status = ProductStatus.PUBLISHED;
    }

    public void addImage(ProductImage image) {
        images.add(image);
        image.setProduct(this);
    }

    public void deleteImage(ProductImage image) {
        images.remove(image);
        image.setProduct(null);
        reorderImages(image.getDisplayOrder());
    }

    private void reorderImages(Integer displayOrder) {
        images.stream()
                .filter(i -> i.getDisplayOrder() > displayOrder)
                .forEach(i -> i.setDisplayOrder(i.getDisplayOrder() - 1));

    }

    public Integer getImagesSize() {
        return images.size();
    }

    public String getCoverImage() {
        return images.stream()
                .filter(i -> i.getDisplayOrder() == 1)
                .findFirst()
                .map(ProductImage::getPublicId)
                .orElseThrow(() -> new EntityNotFoundException("Cover image not found."));
    }

    public List<String> getImagesPublicId() {
        return images.stream()
                .map(ProductImage::getPublicId)
                .toList();
    }
}
