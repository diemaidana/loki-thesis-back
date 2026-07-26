package com.loki.tesis.products;

import com.loki.tesis.auth.credential.entity.Credential;
import com.loki.tesis.auth.credential.service.CredentialService;
import com.loki.tesis.auth.exception.EmailNotVerifiedException;
import com.loki.tesis.auth.exception.ForbiddenException;
import com.loki.tesis.categories.CategoryEntity;
import com.loki.tesis.categories.CategoryRepository;
import com.loki.tesis.productImages.services.ProductImageService;
import com.loki.tesis.products.dtos.request.ProductRequestDto;
import com.loki.tesis.products.dtos.response.ProductCreatedResponseDto;
import com.loki.tesis.products.dtos.response.ProductDetailResponseDto;
import com.loki.tesis.products.dtos.response.ProductSummaryResponseDto;
import com.loki.tesis.user.entity.User;
import com.loki.tesis.user.service.UserService;
import com.loki.tesis.shared.validation.ImageValidator;
import com.loki.tesis.user.entity.User;
import com.loki.tesis.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    private final CategoryRepository categoryRepository;
    private final ProductImageService productImageService;
    private final UserRepository userRepository;

    private final UserService userService;
    private final CredentialService credentialService;

    @Transactional
    public ProductCreatedResponseDto create(ProductRequestDto productRequestDto, Credential credential) {
        /* User user = userRepository.findByUuid(productRequestDto.user())
                .orElseThrow(() -> new EntityNotFoundException("User not found."));
         */

        User seller = credential.getUser();

        validateSellerEmailVerified(seller);

        Set<CategoryEntity> categories = getCategories(productRequestDto.categories());

        validateCategories(categories, productRequestDto.categories());

        Product product = productMapper.toEntity(productRequestDto);
        product.setCategories(categories);
        product.setSeller(user);

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
    public ProductCreatedResponseDto update(UUID productCode, ProductRequestDto request, Credential credential) {

        Product product = getProductEntity(productCode);
        ensureCallerIsSeller(credential, product.getSeller());
        validateSellerEmailVerified(credential.getUser());
        productMapper.updateEntity(request, product);

        Set<CategoryEntity> categories = getCategories(request.categories());
        validateCategories(categories, request.categories());

        product.getCategories().clear();
        product.getCategories().addAll(categories);

        return productMapper.toProductCreatedDto(product);
    }

    public Page<ProductSummaryResponseDto> getAllPublished(String title,
                                                  List<UUID> categoryCodes,
                                                  BigDecimal minPrice,
                                                  BigDecimal maxPrice,
                                                  Pageable page) {

        Specification<Product> specification = getSpecificationFilter(title, categoryCodes, minPrice, maxPrice);

        return productRepository.findAllByStatus(ProductStatus.PUBLISHED, specification, page)
                .map(product -> productMapper.toProductSummaryDto(product, productImageService.getCoverImageUrl(product.getId())));
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
    public void delete(UUID productCode, Credential credential) {

        Product product = getProductEntity(productCode);
        ensureCallerIsSeller(credential, product.getSeller()); // Verifico que el email coincida.
        validateSellerEmailVerified(credential.getUser());

        if(product.getStatus().equals(ProductStatus.DELETED))
            throw new IllegalArgumentException("Product is already deleted.");

        product.setStatus(ProductStatus.DELETED);
    }

    @Transactional
    public ProductSummaryResponseDto uploadImages(UUID productCode, List<MultipartFile> images, Credential credential) {

        for (MultipartFile image : images) {
            if(ImageValidator.isSizeExceeded(image))
                throw new IllegalArgumentException("Images must be under 5MB.");
        }

        Product product = getProductEntity(productCode);
        ensureCallerIsSeller(credential, product.getSeller()); // Verifico que el email coincida.
        validateSellerEmailVerified(credential.getUser());

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

    public List<ProductSummaryResponseDto> getProductsBySellerUuid(UUID sellerUuid) {
        return productRepository.findBySeller_Uuid(sellerUuid)
                .stream()
                .map(p -> productMapper.toProductSummaryDto(p, productImageService.getCoverImageUrl(p.getId())))
                .toList();
    }


    public Page<ProductCreatedResponseDto> getAllUnpublished(String title,
                                                             List<UUID> categoryCodes,
                                                             BigDecimal minPrice,
                                                             BigDecimal maxPrice,
                                                             Pageable page) {

        Specification<Product> specification = getSpecificationFilter(title, categoryCodes, minPrice, maxPrice);

        return productRepository.findAllByStatus(ProductStatus.UNPUBLISHED, specification, page)
                .map(productMapper::toProductCreatedDto);
    }

    public Page<ProductCreatedResponseDto> getAllCreated(String title,
                                                         List<UUID> categoryCodes,
                                                         BigDecimal minPrice,
                                                         BigDecimal maxPrice,
                                                         Pageable page) {

        Specification<Product> specification = getSpecificationFilter(title, categoryCodes, minPrice, maxPrice);

        return productRepository.findAll(specification, page)
                .map(productMapper::toProductCreatedDto);
    }

    private Specification<Product> getSpecificationFilter(String title,
                                                          List<UUID> categoryCodes,
                                                          BigDecimal minPrice,
                                                          BigDecimal maxPrice) {
        return Specification.allOf(
                ProductSpecification.titleContains(title),
                ProductSpecification.hasCategories(categoryCodes),
                ProductSpecification.priceBetween(minPrice, maxPrice)
        );
    }

    // Metodos privados

    // Verifico que el usuario tenga el email verificado.
    private void validateSellerEmailVerified(User seller) {
        if(!credentialService.isEmailVerifiedForUser(seller.getId())){
            throw new EmailNotVerifiedException("Seller's email must be verified.");
        }
    }

    // Verifico que el email de la credencial y producto sean el mismo.
    private void ensureCallerIsSeller(Credential caller, User seller) {
        if(! (Objects.equals(caller.getUser().getUuid(), seller.getUuid()))){
            throw new ForbiddenException("You don't have permission to modify this product.\"");
        }
    }
}
