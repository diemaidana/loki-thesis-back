package com.loki.tesis.shared.address.entity;

import com.loki.tesis.shared.address.enums.Provinces;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class Address {

    @Column(length = 100)
    private String street;

    @Column(length = 20)
    private String streetNumber;

    @Column(length = 10)
    private String floor;

    @Column(length = 10)
    private String apartment;

    @Column(length = 100)
    private String crossStreetOne;

    @Column(length = 100)
    private String crossStreetTwo;

    @Column(length = 100)
    private String city;

    @Column(length = 30)
    @Enumerated(EnumType.STRING)
    private Provinces province;

    @Column(length = 20)
    private String postalCode;

    @Column(length = 255)
    private String references;

}
