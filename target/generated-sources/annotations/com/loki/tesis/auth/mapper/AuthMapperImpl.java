package com.loki.tesis.auth.mapper;

import com.loki.tesis.auth.credential.entity.Credential;
import com.loki.tesis.auth.dto.AccountResponseDTO;
import com.loki.tesis.auth.dto.RegisterRequestDTO;
import com.loki.tesis.shared.address.dto.AddressDTO;
import com.loki.tesis.shared.address.entity.Address;
import com.loki.tesis.shared.address.enums.Provinces;
import com.loki.tesis.user.entity.User;
import com.loki.tesis.user.enums.AccountStatus;
import com.loki.tesis.user.enums.SocialNumberType;
import java.time.Instant;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-03T22:48:53-0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 25.0.4.1 (Amazon.com Inc.)"
)
@Component
public class AuthMapperImpl implements AuthMapper {

    @Override
    public User toUserEntity(RegisterRequestDTO registerRequestDTO) {
        if ( registerRequestDTO == null ) {
            return null;
        }

        User user = new User();

        user.setFirstName( registerRequestDTO.firstName() );
        user.setLastName( registerRequestDTO.lastName() );
        user.setDocumentType( registerRequestDTO.documentType() );
        user.setDocumentNumber( registerRequestDTO.documentNumber() );
        user.setPhoneNumber( registerRequestDTO.phoneNumber() );
        user.setAddress( addressDTOToAddress( registerRequestDTO.address() ) );

        return user;
    }

    @Override
    public Credential toCredentialEntity(RegisterRequestDTO registerRequestDTO) {
        if ( registerRequestDTO == null ) {
            return null;
        }

        Credential credential = new Credential();

        credential.setEmail( registerRequestDTO.email() );
        credential.setPassword( registerRequestDTO.password() );

        return credential;
    }

    @Override
    public AccountResponseDTO toAccountResponseDTO(User user, Credential credential) {
        if ( user == null && credential == null ) {
            return null;
        }

        UUID uuid = null;
        Instant createdAt = null;
        String firstName = null;
        String lastName = null;
        SocialNumberType documentType = null;
        String documentNumber = null;
        AddressDTO address = null;
        AccountStatus status = null;
        if ( user != null ) {
            uuid = user.getUuid();
            createdAt = user.getCreatedAt();
            firstName = user.getFirstName();
            lastName = user.getLastName();
            documentType = user.getDocumentType();
            documentNumber = user.getDocumentNumber();
            address = addressToAddressDTO( user.getAddress() );
            status = user.getStatus();
        }
        String email = null;
        Boolean emailVerified = null;
        if ( credential != null ) {
            email = credential.getEmail();
            emailVerified = credential.isEmailVerified();
        }

        AccountResponseDTO accountResponseDTO = new AccountResponseDTO( uuid, firstName, lastName, email, documentType, documentNumber, address, status, emailVerified, createdAt );

        return accountResponseDTO;
    }

    protected Address addressDTOToAddress(AddressDTO addressDTO) {
        if ( addressDTO == null ) {
            return null;
        }

        Address address = new Address();

        return address;
    }

    protected AddressDTO addressToAddressDTO(Address address) {
        if ( address == null ) {
            return null;
        }

        String street = null;
        String streetNumber = null;
        String floor = null;
        String apartment = null;
        String crossStreetOne = null;
        String crossStreetTwo = null;
        String city = null;
        Provinces province = null;
        String postalCode = null;
        String additionalInformation = null;

        street = address.getStreet();
        streetNumber = address.getStreetNumber();
        floor = address.getFloor();
        apartment = address.getApartment();
        crossStreetOne = address.getCrossStreetOne();
        crossStreetTwo = address.getCrossStreetTwo();
        city = address.getCity();
        province = address.getProvince();
        postalCode = address.getPostalCode();
        additionalInformation = address.getAdditionalInformation();

        AddressDTO addressDTO = new AddressDTO( street, streetNumber, floor, apartment, crossStreetOne, crossStreetTwo, city, province, postalCode, additionalInformation );

        return addressDTO;
    }
}
