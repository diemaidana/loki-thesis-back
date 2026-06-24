package com.loki.tesis.products;

import com.loki.tesis.categories.CategoryEntity;
import com.loki.tesis.categories.CategoryRepository;
import com.loki.tesis.productImages.services.ProductImageService;
import com.loki.tesis.products.dtos.request.ProductRequestDto;
import com.loki.tesis.products.dtos.response.ProductCreatedResponseDto;
import com.loki.tesis.products.dtos.response.ProductDetailResponseDto;
import com.loki.tesis.products.dtos.response.ProductSummaryResponseDto;
import com.loki.tesis.shared.validation.ImageValidator;
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
    public ProductCreatedResponseDto create(ProductRequestDto productRequestDto) {
        // User validations missing

        Set<CategoryEntity> categories = getCategories(productRequestDto.categories());

        validateCategories(categories, productRequestDto.categories());

        Product product = productMapper.toEntity(productRequestDto);
        product.setCategories(categories);

        return productMapper.toProductCreatedDto(productRepository.save(product));
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
    public ProductCreatedResponseDto update(UUID productCode, ProductRequestDto request) {
        Product product = getProductEntity(productCode);

        productMapper.updateEntity(request, product);

        Set<CategoryEntity> categories = getCategories(request.categories());
        validateCategories(categories, request.categories());

        product.getCategories().clear();
        product.getCategories().addAll(categories);

        return productMapper.toProductCreatedDto(product);
    }

    public List<ProductSummaryResponseDto> getAll() {
        return productRepository.findAll()
                .stream()
                .map(p -> productMapper.toProductSummaryDto(p, productImageService.getCoverImageUrl(p.getId())))
                .toList();
    }

    public ProductDetailResponseDto getProductDetailsByProductCode(UUID productCode) {
        Product product = getProductEntity(productCode);
        List<String> imagesUrl = productImageService.getImages(product.getId());
        return productMapper.toProductDetailDto(product, imagesUrl);
    }

    public ProductCreatedResponseDto getProductCreatedByProductCode(UUID productCode) {
        return productMapper.toProductCreatedDto(getProductEntity(productCode));
    }

    public ProductSummaryResponseDto getProductPublishByCode(UUID productCode) {
        Product product = getProductEntity(productCode);
        String imageUrl = productImageService.getCoverImageUrl(product.getId());
        return productMapper.toProductSummaryDto(product, imageUrl);
    }

    private Product getProductEntity(UUID productCode) {
        return productRepository.findByProductCode(productCode)
                .orElseThrow(() -> new EntityNotFoundException("Product not found."));
    }

    @Transactional
    public void delete(UUID productCode) {
        Product product = getProductEntity(productCode);

        product.setStatus(ProductStatus.UNPUBLISHED);
    }

    @Transactional
    public ProductSummaryResponseDto uploadImages(UUID productCode, List<MultipartFile> images) {

        for (MultipartFile image : images) {
            if(ImageValidator.isSizeExceeded(image))
                throw new IllegalArgumentException("Images must be under 5MB.");
        }

        Product product = getProductEntity(productCode);

        if(images.isEmpty() || images.size() > 5)
            throw new IllegalArgumentException("At least one image must be provided.");

        List<String> filenames = images.stream()
                .map(productImageService::storeImage)
                .toList();

        for(int i = 0; i < filenames.size(); i++)
            productImageService.create(filenames.get(i), product, i);

        product.publish();

        return productMapper.toProductSummaryDto(product, filenames.getFirst());
    }
}
