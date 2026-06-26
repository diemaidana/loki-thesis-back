package com.loki.tesis.products;

import com.loki.tesis.products.dtos.request.ProductRequestDto;
import com.loki.tesis.products.dtos.response.ProductCreatedResponseDto;
import com.loki.tesis.products.dtos.response.ProductDetailResponseDto;
import com.loki.tesis.products.dtos.response.ProductSummaryResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductCreatedResponseDto> create(@Valid @RequestBody ProductRequestDto productRequestDto) {
        ProductCreatedResponseDto productCreatedResponseDto = productService.create(productRequestDto);

        return ResponseEntity.ok(productCreatedResponseDto);
    }

    @PutMapping("/{productCode}")
    public ResponseEntity<ProductCreatedResponseDto> update(@PathVariable UUID productCode,
                                                            @Valid @RequestBody ProductRequestDto request) {
        return ResponseEntity.ok(productService.update(productCode, request));
    }

    @GetMapping
    public ResponseEntity<Page<ProductCreatedResponseDto>> getAll(@RequestParam(required = false) String title,
                                                                  @RequestParam(required = false) List<UUID> categoryCodes,
                                                                  @RequestParam(required = false) BigDecimal minPrice,
                                                                  @RequestParam(required = false) BigDecimal maxPrice,
                                                                  Pageable page) {
        return ResponseEntity.ok(productService.getAllCreated(title, categoryCodes, minPrice, maxPrice, page));
    }

    @GetMapping("/published")
    public ResponseEntity<Page<ProductSummaryResponseDto>> getAllPublished(@RequestParam(required = false) String title,
                                                                  @RequestParam(required = false) List<UUID> categoryCodes,
                                                                  @RequestParam(required = false) BigDecimal minPrice,
                                                                  @RequestParam(required = false) BigDecimal maxPrice,
                                                                  Pageable page) {
        return ResponseEntity.ok(productService.getAllPublished(title, categoryCodes, minPrice, maxPrice, page));
    }

    @GetMapping("/unpublished")
    public ResponseEntity<Page<ProductCreatedResponseDto>> getAllUnpublished(@RequestParam(required = false) String title,
                                                                           @RequestParam(required = false) List<UUID> categoryCodes,
                                                                           @RequestParam(required = false) BigDecimal minPrice,
                                                                           @RequestParam(required = false) BigDecimal maxPrice,
                                                                           Pageable page) {
        return ResponseEntity.ok(productService.getAllUnpublished(title, categoryCodes, minPrice, maxPrice, page));
    }

    @GetMapping("/{productCode}")
    public ResponseEntity<ProductDetailResponseDto> getProductDetailByProductCode(@PathVariable UUID productCode) {
        return ResponseEntity.ok(productService.getProductDetailsByProductCode(productCode));
    }

    @GetMapping("/created/{productCode}")
    public ResponseEntity<ProductCreatedResponseDto> getProductCreatedByProductCode(@PathVariable UUID productCode) {
        return ResponseEntity.ok(productService.getProductCreatedByProductCode(productCode));
    }

    @DeleteMapping("/{productCode}")
    public ResponseEntity<Void> delete(@PathVariable UUID productCode) {
        productService.delete(productCode);

        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{productCode}/uploadImages", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductSummaryResponseDto> uploadImages(@PathVariable UUID productCode, @RequestPart List<MultipartFile> images) {
        return ResponseEntity.ok(productService.uploadImages(productCode, images));
    }
}
