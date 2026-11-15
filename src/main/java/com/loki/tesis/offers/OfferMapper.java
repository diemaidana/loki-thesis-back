package com.loki.tesis.offers;

import com.loki.tesis.offers.dto.CreateOfferRequestDto;
import com.loki.tesis.offers.dto.OfferResponseDTO;
import com.loki.tesis.products.ProductMapper;
import com.loki.tesis.user.mapper.UserMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ProductMapper.class, UserMapper.class})
public interface OfferMapper {

    @Mapping(target = "buyer", ignore = true)
    @Mapping(target = "product", ignore = true)
    OfferEntity toEntity(CreateOfferRequestDto request);


    OfferResponseDTO toResponseDTO(OfferEntity offerEntity);
}
