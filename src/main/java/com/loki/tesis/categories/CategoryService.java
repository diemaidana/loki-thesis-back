package com.loki.tesis.categories;

import com.loki.tesis.categories.dto.CategoryRequestDTO;
import com.loki.tesis.categories.dto.CategoryResponseDTO;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

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

    @Transactional
    public CategoryResponseDTO update(UUID categoryCode, CategoryRequestDTO requestDTO) {
        CategoryEntity category = findCategory(categoryCode);

        category.setName(requestDTO.name());
        category.setDescription(requestDTO.description());

        return categoryMapper.toDto(category);
    }

    private CategoryEntity findCategory(UUID categoryCode) {
        return categoryRepository.findByCategoryCode(categoryCode)
                .orElseThrow(() -> new EntityNotFoundException("Category not found."));
    }

    public List<CategoryResponseDTO> getAll() {
        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toDto)
                .toList();
    }

    @Transactional
    public void delete(UUID categoryCode) {
        CategoryEntity category = categoryRepository.findByCategoryCode(categoryCode)
                .orElseThrow(() -> new EntityNotFoundException("Category not found."));

        category.setStatus(Boolean.FALSE);
    }
}
