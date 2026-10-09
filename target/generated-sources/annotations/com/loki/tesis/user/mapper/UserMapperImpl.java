package com.loki.tesis.user.mapper;

import com.loki.tesis.shared.address.dto.AddressDTO;
import com.loki.tesis.shared.address.entity.Address;
import com.loki.tesis.shared.address.enums.Provinces;
import com.loki.tesis.shared.address.mapper.AddressMapper;
import com.loki.tesis.user.dto.UserResponseDTO;
import com.loki.tesis.user.dto.UserUpdateDTO;
import com.loki.tesis.user.entity.User;
import com.loki.tesis.user.enums.AccountStatus;
import com.loki.tesis.user.enums.SocialNumberType;
import java.time.Instant;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-08T22:23:19-0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 25.0.4.1 (Amazon.com Inc.)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Autowired
    private AddressMapper addressMapper;

    @Override
    public UserResponseDTO toUserResponseDTO(User user) {
        if ( user == null ) {
            return null;
        }

        UUID uuid = null;
        String firstName = null;
        String lastName = null;
        SocialNumberType documentType = null;
        String documentNumber = null;
        AddressDTO address = null;
        AccountStatus status = null;
        Instant createdAt = null;

        uuid = user.getUuid();
        firstName = user.getFirstName();
        lastName = user.getLastName();
        documentType = user.getDocumentType();
        documentNumber = user.getDocumentNumber();
        address = addressToAddressDTO( user.getAddress() );
        status = user.getStatus();
        createdAt = user.getCreatedAt();

        UserResponseDTO userResponseDTO = new UserResponseDTO( uuid, firstName, lastName, documentType, documentNumber, address, status, createdAt );

        return userResponseDTO;
    }

    @Override
    public void updateUserFromDTO(UserUpdateDTO userUpdateDTO, User user) {
        if ( userUpdateDTO == null ) {
            return;
        }

        if ( userUpdateDTO.firstName() != null ) {
            user.setFirstName( userUpdateDTO.firstName() );
        }
        if ( userUpdateDTO.lastName() != null ) {
            user.setLastName( userUpdateDTO.lastName() );
        }
        if ( userUpdateDTO.phoneNumber() != null ) {
            user.setPhoneNumber( userUpdateDTO.phoneNumber() );
        }
        if ( userUpdateDTO.address() != null ) {
            user.setAddress( addressMapper.toAddress( userUpdateDTO.address() ) );
        }
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
