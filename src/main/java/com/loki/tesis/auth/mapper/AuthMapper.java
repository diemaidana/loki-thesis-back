package com.loki.tesis.auth.mapper;

import com.loki.tesis.auth.credential.entity.Credential;
import com.loki.tesis.auth.dto.response.AccountResponseDTO;
import com.loki.tesis.auth.dto.response.LoginResponseDTO;
import com.loki.tesis.auth.dto.request.RegisterRequestDTO;
import com.loki.tesis.shared.address.mapper.AddressMapper;
import com.loki.tesis.user.entity.User;
import org.mapstruct.*;

import java.time.Instant;

@Mapper(
            componentModel = "spring",
            unmappedTargetPolicy = ReportingPolicy.ERROR,
            uses = AddressMapper.class
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
    @Mapping(target = "loginAttempts", ignore = true)
    @Mapping(target = "lockedUntil", ignore = true)
    @Mapping(target = "lastLockNotificationAt", ignore = true)
    @Mapping(target = "roleType", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    @Mapping(target = "version", ignore = true)
    Credential toCredentialEntity(RegisterRequestDTO registerRequestDTO);

    @Mapping(source = "user.uuid", target = "uuid")
    @Mapping(source = "credential.email", target = "email")
    @Mapping(source = "credential.emailVerified", target = "emailVerified")
    @Mapping(source = "user.createdAt", target = "createdAt")
    AccountResponseDTO toAccountResponseDTO(User user, Credential credential);

    @Mapping(source = "accountResponseDTO", target = "account")
    @Mapping(source = "token", target = "token")
    @Mapping(source = "expiresAt", target = "expiresAt")
    LoginResponseDTO toLoginResponseDTO(AccountResponseDTO accountResponseDTO, String token,  Instant expiresAt);
}