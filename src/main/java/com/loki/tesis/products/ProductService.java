package com.loki.tesis.products;

import com.loki.tesis.categories.CategoryEntity;
import com.loki.tesis.categories.CategoryRepository;
import com.loki.tesis.products.dtos.request.ProductRequestDto;
import com.loki.tesis.products.dtos.response.ProductResponseDto;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    private final CategoryRepository categoryRepository;

    @Transactional
    public ProductResponseDto create(ProductRequestDto productRequestDto) {
        // User validations missing

        Set<CategoryEntity> categories = getCategories(productRequestDto.categories());

        validateCategories(categories, productRequestDto.categories());

        Product product = productMapper.toEntity(productRequestDto);
        product.setCategories(categories);

        return productMapper.toDto(productRepository.save(product));
    }

    private void validateCategories(Set<CategoryEntity> categoriesFound, Set<UUID> categoriesRequest) {
        if(categoriesFound.size() != categoriesRequest.size())
            throw new IllegalArgumentException("One or more categories do not exist.");
    }

    private Set<CategoryEntity> getCategories(Set<UUID> categories) {
        return new HashSet<>(
                categoryRepository.findAllByCategoryCodeIn(categories)
        );
    }

    @Transactional
    public ProductResponseDto update(UUID productCode, ProductRequestDto request) {
        Product product = getProductEntity(productCode);

        productMapper.updateEntity(request, product);

        Set<CategoryEntity> categories = getCategories(request.categories());
        validateCategories(categories, request.categories());

        product.getCategories().clear();
        product.getCategories().addAll(categories);

        return productMapper.toDto(product);
    }

    public List<ProductResponseDto> getAll() {
        return productRepository.findAll()
                .stream()
                .map(productMapper::toDto)
                .toList();
    }

    public ProductResponseDto getByProductCode(UUID productCode) {
        return productMapper.toDto(getProductEntity(productCode));
    }

    private Product getProductEntity(UUID productCode) {
        return productRepository.findByProductCode(productCode)
                .orElseThrow(() -> new EntityNotFoundException("Product not found."));
    }

    @Transactional
    public void delete(UUID productCode) {
        Product product = getProductEntity(productCode);

        product.setStatus(Boolean.FALSE);
    }
}
