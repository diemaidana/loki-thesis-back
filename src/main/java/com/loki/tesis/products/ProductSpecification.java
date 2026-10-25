package com.loki.tesis.products;

import com.loki.tesis.categories.CategoryEntity;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class ProductSpecification {

    // Filters products by title
    public static Specification<Product> titleContains(String title) {
        return (root, query, cb) -> {
            if(title == null || title.isBlank()) return cb.conjunction();
            return cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
        };
    }

    // Filters products by price.
    public static Specification<Product> priceBetween(BigDecimal minPrice, BigDecimal maxPrice) {
        return (root, query, cb) -> {
            if(minPrice == null && maxPrice == null) return cb.conjunction();
            if(minPrice == null) return cb.lessThanOrEqualTo(root.get("price"), maxPrice);
            if(maxPrice == null) return cb.greaterThanOrEqualTo(root.get("price"), minPrice);
            return cb.between(root.get("price"), minPrice, maxPrice);
        };
    }

    /*
    - Filters products by category codes (UUID).
    - INNER JOIN -> returns only products with matching category.
    - Specification instead of PredicateSpecification to prevent dupplicates.
     */
    public static Specification<Product> hasCategories(List<UUID> categoryCodes) {
        return (root, query, cb) -> {
            if(categoryCodes == null || categoryCodes.isEmpty()) return cb.conjunction();

            query.distinct(true);
            Join<Product, CategoryEntity> categories = root.join("categories", JoinType.INNER);
            return categories.get("categoryCode").in(categoryCodes);
        };
    }
}
