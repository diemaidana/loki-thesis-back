package com.loki.tesis.categories;

import com.loki.tesis.categories.dto.CategoryRequestDTO;
import com.loki.tesis.categories.dto.CategoryResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "categoryCode", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    CategoryEntity toEntity(CategoryRequestDTO request);

    CategoryResponseDTO toDto(CategoryEntity categoryEntity);
}
