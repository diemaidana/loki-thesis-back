package com.loki.tesis.products;

import com.loki.tesis.products.dtos.request.ProductRequestDto;
import com.loki.tesis.products.dtos.response.ProductResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    Product toEntity(ProductRequestDto productRequestDto);
    ProductResponseDto toDto(Product product);

}
