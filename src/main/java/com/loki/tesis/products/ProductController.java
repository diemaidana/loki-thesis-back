package com.loki.tesis.products;

import com.loki.tesis.products.dtos.request.ProductRequestDto;
import com.loki.tesis.products.dtos.response.ProductCreatedResponseDto;
import com.loki.tesis.products.dtos.response.ProductDetailResponseDto;
import com.loki.tesis.products.dtos.response.ProductSummaryResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public ResponseEntity<List<ProductSummaryResponseDto>> getAll() {
        return ResponseEntity.ok(productService.getAll());
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
