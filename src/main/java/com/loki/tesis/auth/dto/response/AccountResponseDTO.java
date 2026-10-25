package com.loki.tesis.auth.dto.response;

import com.loki.tesis.shared.address.dto.AddressDTO;
import com.loki.tesis.user.enums.AccountStatus;
import com.loki.tesis.user.enums.SocialNumberType;

import java.time.Instant;
import java.util.UUID;

public record AccountResponseDTO(
        UUID uuid,
        String firstName,
        String lastName,
        String email,
        SocialNumberType documentType,
        String documentNumber,
        AddressDTO address,
        AccountStatus status,
        Boolean emailVerified,
        Instant createdAt
) {}