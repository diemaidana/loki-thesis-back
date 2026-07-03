package com.loki.tesis.products;

import com.loki.tesis.auth.credential.entity.Credential;
import com.loki.tesis.products.dtos.request.ProductRequestDto;
import com.loki.tesis.products.dtos.response.ProductCreatedResponseDto;
import com.loki.tesis.products.dtos.response.ProductDetailResponseDto;
import com.loki.tesis.products.dtos.response.ProductSummaryResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<ProductCreatedResponseDto> create(@Valid @RequestBody ProductRequestDto productRequestDto,
                                                            @AuthenticationPrincipal Credential credential) {
        ProductCreatedResponseDto productCreatedResponseDto = productService.create(productRequestDto, credential);

        return ResponseEntity.ok(productCreatedResponseDto);
    }

    @PutMapping("/{productCode}")
    public ResponseEntity<ProductCreatedResponseDto> update(@PathVariable UUID productCode,
                                                            @Valid @RequestBody ProductRequestDto request,
                                                            @AuthenticationPrincipal Credential credential) {
        return ResponseEntity.ok(productService.update(productCode, request, credential));
    }

    @GetMapping
    public ResponseEntity<List<ProductSummaryResponseDto>> getAll(@RequestParam(required = false) UUID sellerUuid) {
        List<ProductSummaryResponseDto> products;

        if(sellerUuid == null) {
            products = productService.getAll();
        } else {
            products = productService.getProductsBySellerUuid(sellerUuid);
        }

        return ResponseEntity.ok(products);
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
    public ResponseEntity<Void> delete(@PathVariable UUID productCode,
                                       @AuthenticationPrincipal Credential credential) {
        productService.delete(productCode, credential);

        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{productCode}/uploadImages", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductSummaryResponseDto> uploadImages(@PathVariable UUID productCode,
                                                                  @RequestPart List<MultipartFile> images,
                                                                  @AuthenticationPrincipal Credential credential) {
        return ResponseEntity.ok(productService.uploadImages(productCode, images, credential));
    }
}
