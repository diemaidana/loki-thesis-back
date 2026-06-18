package com.loki.tesis.user.dto;

import com.loki.tesis.shared.address.dto.AddressDTO;
import com.loki.tesis.user.enums.AccountStatus;
import com.loki.tesis.user.enums.SocialNumberType;

import java.time.Instant;
import java.util.UUID;

public record UserResponseDTO(
        UUID uuid,
        String firstName,
        String lastName,
        SocialNumberType documentType,
        String documentNumber,
        AddressDTO address,
        AccountStatus status,
        Instant createdAt
) {}
