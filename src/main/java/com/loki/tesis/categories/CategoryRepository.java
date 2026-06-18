package com.loki.tesis.categories;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    Boolean existsByName(String name);

    Optional<CategoryEntity> findByCategoryCode(UUID categoryCode);

    List<CategoryEntity> findAllByCategoryCodeIn(Set<UUID> categoryCodes);
}
