package com.loki.tesis.offers;

import com.loki.tesis.offers.dto.CreateOfferRequestDto;
import com.loki.tesis.offers.dto.CreateOfferResponseDTO;
import com.loki.tesis.products.ProductMapper;
import com.loki.tesis.user.mapper.UserMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ProductMapper.class, UserMapper.class})
public interface OfferMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "offerCode", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "lastUserType", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "expiresAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "seller", ignore = true)
    @Mapping(target = "buyer", ignore = true)
    @Mapping(target = "product", ignore = true)
    OfferEntity toEntity(CreateOfferRequestDto request);



    @Mapping(target = "product.coverImageUrl", ignore = true)
    CreateOfferResponseDTO toCreateResponseDTO(OfferEntity offerEntity);
}
