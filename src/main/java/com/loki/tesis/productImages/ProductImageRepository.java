package com.loki.tesis.productImages;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    @Query("SELECT i.url FROM ProductImage i WHERE i.product.id = :productId AND i.displayOrder = :displayOrder")
    Optional<String> findImageUrl(@Param("productId") Long productId, @Param("displayOrder") int displayOrder);

    @Query("SELECT i.url FROM ProductImage i WHERE i.product.id = :productId")
    List<String> findImagesUrl(@Param("productId")Long productId);
}
