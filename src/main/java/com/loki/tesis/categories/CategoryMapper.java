package com.loki.tesis.categories;

import com.loki.tesis.categories.dto.CategoryRequestDTO;
import com.loki.tesis.categories.dto.CategoryResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryEntity toEntity(CategoryRequestDTO request);

    CategoryResponseDTO toDto(CategoryEntity categoryEntity);
}
