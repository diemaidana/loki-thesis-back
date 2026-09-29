package com.loki.tesis.products;

import io.micrometer.core.instrument.config.MeterFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    Optional<Product> findByProductCode(UUID productCode);
    List<Product> findBySeller_Uuid(UUID sellerUuid);

    Page<Product> findAllByStatus(ProductStatus productStatus, Specification<Product> specification, Pageable page);
}
