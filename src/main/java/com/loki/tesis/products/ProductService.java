package com.loki.tesis.products;

import com.loki.tesis.products.dtos.request.ProductRequestDto;
import com.loki.tesis.products.dtos.response.ProductResponseDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Transactional
    public ProductResponseDto create(ProductRequestDto productRequestDto) {
        // User validations missing
        // Categories validations missing

        Product product = productMapper.toEntity(productRequestDto);

        return productMapper.toDto(productRepository.save(product));
    }
}
