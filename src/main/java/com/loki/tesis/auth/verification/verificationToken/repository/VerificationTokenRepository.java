package com.loki.tesis.auth.verification.verificationToken.repository;

import com.loki.tesis.auth.credential.entity.Credential;
import com.loki.tesis.auth.verification.verificationToken.entity.VerificationToken;
import com.loki.tesis.auth.verification.verificationToken.enums.TokenType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByToken(String token);

    // Optional<VerificationToken> findByCredentialAndTokenTypeAndUsedAtIsNull(Credential credential, TokenType tokenType);

    @Modifying
    @Query(
            "UPDATE VerificationToken t SET t.usedAt = :now " +
            "WHERE t.credential = :credential " +
            "AND t.tokenType = :tokenType " +
            "AND t.usedAt IS NULL"
    )
    void invalidateActiveTokens(
            @Param("credential") Credential credential,
            @Param("tokenType") TokenType tokenType,
            @Param("now") Instant now
    );
}
