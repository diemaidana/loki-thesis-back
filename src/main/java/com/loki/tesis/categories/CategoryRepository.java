package com.loki.tesis.categories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    Boolean existsByName(String name);

    Optional<CategoryEntity> findByCategoryCode(UUID categoryCode);
}
