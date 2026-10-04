package com.loki.tesis.products;

import com.loki.tesis.products.dtos.request.ProductRequestDto;
import com.loki.tesis.products.dtos.response.ProductCreatedResponseDto;
import com.loki.tesis.products.dtos.response.ProductDetailResponseDto;
import com.loki.tesis.products.dtos.response.ProductSummaryResponseDto;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-03T22:48:53-0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 25.0.4.1 (Amazon.com Inc.)"
)
@Component
public class ProductMapperImpl implements ProductMapper {

    @Override
    public Product toEntity(ProductRequestDto productRequestDto) {
        if ( productRequestDto == null ) {
            return null;
        }

        Product product = new Product();

        product.setTitle( productRequestDto.title() );
        product.setDescription( productRequestDto.description() );
        product.setPrice( productRequestDto.price() );
        product.setPriceMin( productRequestDto.priceMin() );
        product.setStock( productRequestDto.stock() );

        return product;
    }

    @Override
    public void updateEntity(ProductRequestDto request, Product product) {
        if ( request == null ) {
            return;
        }

        product.setTitle( request.title() );
        product.setDescription( request.description() );
        product.setPrice( request.price() );
        product.setPriceMin( request.priceMin() );
        product.setStock( request.stock() );
    }

    @Override
    public ProductCreatedResponseDto toProductCreatedDto(Product product) {
        if ( product == null ) {
            return null;
        }

        UUID productCode = null;
        String title = null;
        String description = null;
        BigDecimal price = null;
        LocalDateTime createdAt = null;

        productCode = product.getProductCode();
        title = product.getTitle();
        description = product.getDescription();
        price = product.getPrice();
        createdAt = product.getCreatedAt();

        ProductCreatedResponseDto productCreatedResponseDto = new ProductCreatedResponseDto( productCode, title, description, price, createdAt );

        return productCreatedResponseDto;
    }

    @Override
    public ProductSummaryResponseDto toProductSummaryDto(Product product, String coverImageUrl) {
        if ( product == null && coverImageUrl == null ) {
            return null;
        }

        UUID productCode = null;
        String title = null;
        String description = null;
        BigDecimal price = null;
        LocalDateTime createdAt = null;
        if ( product != null ) {
            productCode = product.getProductCode();
            title = product.getTitle();
            description = product.getDescription();
            price = product.getPrice();
            createdAt = product.getCreatedAt();
        }
        String coverImageUrl1 = null;
        coverImageUrl1 = coverImageUrl;

        ProductSummaryResponseDto productSummaryResponseDto = new ProductSummaryResponseDto( productCode, title, description, price, createdAt, coverImageUrl1 );

        return productSummaryResponseDto;
    }

    @Override
    public ProductDetailResponseDto toProductDetailDto(Product product, List<String> imagesUrl) {
        if ( product == null && imagesUrl == null ) {
            return null;
        }

        UUID productCode = null;
        String title = null;
        String description = null;
        BigDecimal price = null;
        LocalDateTime createdAt = null;
        if ( product != null ) {
            productCode = product.getProductCode();
            title = product.getTitle();
            description = product.getDescription();
            price = product.getPrice();
            createdAt = product.getCreatedAt();
        }
        List<String> images = null;
        List<String> list = imagesUrl;
        if ( list != null ) {
            images = new ArrayList<String>( list );
        }

        ProductDetailResponseDto productDetailResponseDto = new ProductDetailResponseDto( productCode, title, description, price, createdAt, images );

        return productDetailResponseDto;
    }
}
