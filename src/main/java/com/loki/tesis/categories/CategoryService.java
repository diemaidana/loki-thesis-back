package com.loki.tesis.categories;

import com.loki.tesis.categories.dto.CategoryRequestDTO;
import com.loki.tesis.categories.dto.CategoryResponseDTO;
import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Transactional
    public CategoryResponseDTO create(CategoryRequestDTO requestDTO) {
        if(categoryRepository.existsByName(requestDTO.name()))
            throw new EntityExistsException(requestDTO.name() + " category already exists.");

        CategoryEntity category = categoryMapper.toEntity(requestDTO);

        return categoryMapper.toDto(categoryRepository.save(category));
    }
}
