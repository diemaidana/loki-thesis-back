package com.loki.tesis.products;

import com.loki.tesis.categories.CategoryEntity;
import com.loki.tesis.categories.CategoryRepository;
import com.loki.tesis.products.dtos.request.ProductRequestDto;
import com.loki.tesis.products.dtos.response.ProductResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
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

        Set<CategoryEntity> categories = new HashSet<>(
                categoryRepository.findAllByCategoryCodeIn(productRequestDto.categories())
        );

        if(categories.size() != productRequestDto.categories().size())
            throw new IllegalArgumentException("One or more categories do not exist.");

        Product product = productMapper.toEntity(productRequestDto);
        product.setCategories(categories);

        return productMapper.toDto(productRepository.save(product));
    }
}
