package com.loki.tesis.auth.mapper;

import com.loki.tesis.auth.credential.entity.Credential;
import com.loki.tesis.auth.dto.AccountResponseDTO;
import com.loki.tesis.auth.dto.RegisterRequestDTO;
import com.loki.tesis.user.entity.User;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface AuthMapper {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "documentType", source = "documentType")
    @Mapping(target = "documentNumber", source = "documentNumber")
    @Mapping(target = "phoneNumber", source = "phoneNumber")
    @Mapping(target = "address", source = "address")
    User toUserEntity(RegisterRequestDTO registerRequestDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "emailVerified", ignore = true)
    Credential toCredentialEntity(RegisterRequestDTO registerRequestDTO);

    @Mapping(source = "user.uuid", target = "uuid")
    @Mapping(source = "credential.email", target = "email")
    @Mapping(source = "credential.emailVerified", target = "emailVerified")
    @Mapping(source = "user.createdAt", target = "createdAt")
    AccountResponseDTO toAccountResponseDTO(User user, Credential credential);
}