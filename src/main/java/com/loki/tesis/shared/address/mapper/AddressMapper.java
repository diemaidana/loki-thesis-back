package com.loki.tesis.shared.address.mapper;

import com.loki.tesis.shared.address.dto.AddressDTO;
import com.loki.tesis.shared.address.entity.Address;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AddressMapper {
    Address toAddress(AddressDTO  addressDTO);
}
