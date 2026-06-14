package com.loki.tesis.products;

import com.loki.tesis.categories.CategoryEntity;
import com.loki.tesis.categories.CategoryRepository;
import com.loki.tesis.productImages.services.ProductImageService;
import com.loki.tesis.products.dtos.request.ProductRequestDto;
import com.loki.tesis.products.dtos.response.ProductResponseDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    private final CategoryRepository categoryRepository;
    private final ProductImageService productImageService;

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

    @Transactional
    public ProductResponseDto uploadImages(UUID productCode, List<MultipartFile> images) {
        Product product = getProductEntity(productCode);

        if(images.isEmpty() || images.size() > 5)
            throw new IllegalArgumentException("At least one image must be provided.");

        List<String> filenames = images.stream()
                .map(productImageService::storeImage)
                .toList();

        for(int i = 0; i < filenames.size(); i++)
            productImageService.create(filenames.get(i), product, i);

        // product.setStatus();

        return productMapper.toDto(product);
    }
}
