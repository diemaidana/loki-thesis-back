package com.loki.tesis.offers;

import com.loki.tesis.offers.dto.CreateOfferRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OfferMapper {

    @Mapping(target = "buyer", ignore = true)
    @Mapping(target = "product", ignore = true)
    OfferEntity toEntity(CreateOfferRequestDto request);
}
