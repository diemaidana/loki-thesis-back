package com.loki.tesis.products;

import com.loki.tesis.products.dtos.request.ProductRequestDto;
import com.loki.tesis.products.dtos.response.ProductResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponseDto> create(@Valid @RequestBody ProductRequestDto productRequestDto) {
        ProductResponseDto productResponseDto = productService.create(productRequestDto);

        return ResponseEntity.ok(productResponseDto);
    }

    @PutMapping("/{productCode}")
    public ResponseEntity<ProductResponseDto> update(@PathVariable UUID productCode,
                                                     @Valid @RequestBody ProductRequestDto request) {
        return ResponseEntity.ok(productService.update(productCode, request));
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> getAll() {
        return ResponseEntity.ok(productService.getAll());
    }

    @GetMapping("/{productCode}")
    public ResponseEntity<ProductResponseDto> getByProductCode(@PathVariable UUID productCode) {
        return ResponseEntity.ok(productService.getByProductCode(productCode));
    }

    @DeleteMapping("/{productCode}")
    public ResponseEntity<Void> delete(@PathVariable UUID productCode) {
        productService.delete(productCode);

        return ResponseEntity.noContent().build();
    }
}
