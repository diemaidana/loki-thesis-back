package com.loki.tesis.user.mapper;

import com.loki.tesis.user.dto.UserResponseDTO;
import com.loki.tesis.user.dto.UserUpdateDTO;
import com.loki.tesis.user.entity.User;
import org.mapstruct.*;

@Mapper (
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
        )
public interface UserMapper {

    UserResponseDTO toUserResponseDTO(User user);

    @BeanMapping(
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
            ignoreByDefault = true
    )
    // Whitelist: solo se mapean los 4 campos editables, el resto se ignora por defecto.
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "phoneNumber", source = "phoneNumber")
    @Mapping(target = "address", source = "address")
    void updateUserFromDTO(UserUpdateDTO userUpdateDTO, @MappingTarget User user);
}
