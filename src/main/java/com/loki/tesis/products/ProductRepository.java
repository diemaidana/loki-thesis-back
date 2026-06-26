package com.loki.tesis.products;

import io.micrometer.core.instrument.config.MeterFilter;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    Optional<Product> findByProductCode(UUID productCode);

    Page<Product> findAllByStatus(ProductStatus productStatus, Specification<Product> specification, Pageable page);

    Optional<Product> findByProductCodeAndUserId(UUID productCode, Long userId);
}
