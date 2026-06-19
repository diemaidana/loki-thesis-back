package com.loki.tesis.auth.verification.verificationToken.entity;

import com.loki.tesis.auth.credential.entity.Credential;
import com.loki.tesis.auth.verification.verificationToken.enums.TokenType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "verification_tokens")
@Getter
@Setter
@NoArgsConstructor
public class VerificationToken {
    // TODO: scheduler que limpie tokens vencidos

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "verification_tokens_id_seq")
    @SequenceGenerator(name = "verification_tokens_id_seq", sequenceName = "verification_tokens_id_seq", allocationSize = 1)
    private Long id;

    @Column(unique = true, nullable = false, length = 64)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TokenType tokenType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "credential_id", nullable = false)
    private Credential credential;

    @Column(nullable = false, updatable = false, name = "created_at")
    private Instant createdAt;

    @Column(nullable = false, name = "expires_at")
    private Instant expiresAt;

    @Column(name = "used_at")
    private Instant usedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = Instant.now();
    }
}
