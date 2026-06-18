package com.loki.tesis.user.entity;

import com.loki.tesis.shared.address.entity.Address;
import com.loki.tesis.user.enums.AccountStatus;
import com.loki.tesis.user.enums.SocialNumberType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {

    // ID privado para la base de datos. Es auto-incremental para facilidad en la busqueda, inserciòn, entre otras.
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    // ID publico no coincide con la base de datos para seguridad y busqueda.
    @UuidGenerator
    @Column(unique = true, nullable = false, updatable = false)
    private UUID uuid;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private SocialNumberType documentType;

    @Column(unique = true, nullable = false, length = 11)
    private String documentNumber;

    @Column(nullable = false, length = 50)
    private String firstName;

    @Column(nullable = false, length = 50)
    private String lastName;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private AccountStatus status = AccountStatus.ACTIVE;

    // Datos de contacto
    @Column(length = 20)
    private String phoneNumber;

    @Embedded
    private Address address;

}
