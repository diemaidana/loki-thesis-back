package com.loki.tesis.categories;

import com.loki.tesis.categories.dto.CategoryRequestDTO;
import com.loki.tesis.categories.dto.CategoryResponseDTO;
import java.time.LocalDateTime;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-06T11:02:51-0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 25.0.2 (Oracle Corporation)"
)
@Component
public class CategoryMapperImpl implements CategoryMapper {

    @Override
    public CategoryEntity toEntity(CategoryRequestDTO request) {
        if ( request == null ) {
            return null;
        }

        CategoryEntity categoryEntity = new CategoryEntity();

        categoryEntity.setName( request.name() );
        categoryEntity.setDescription( request.description() );

        return categoryEntity;
    }

    @Override
    public CategoryResponseDTO toDto(CategoryEntity categoryEntity) {
        if ( categoryEntity == null ) {
            return null;
        }

        UUID categoryCode = null;
        String name = null;
        String description = null;
        Boolean status = null;
        LocalDateTime createdAt = null;
        LocalDateTime updatedAt = null;

        categoryCode = categoryEntity.getCategoryCode();
        name = categoryEntity.getName();
        description = categoryEntity.getDescription();
        status = categoryEntity.getStatus();
        createdAt = categoryEntity.getCreatedAt();
        updatedAt = categoryEntity.getUpdatedAt();

        CategoryResponseDTO categoryResponseDTO = new CategoryResponseDTO( categoryCode, name, description, status, createdAt, updatedAt );

        return categoryResponseDTO;
    }
}
