package com.loki.tesis.auth.verification.verificationToken.repository;

import com.loki.tesis.auth.credential.entity.Credential;
import com.loki.tesis.auth.verification.verificationToken.entity.VerificationToken;
import com.loki.tesis.auth.verification.verificationToken.enums.TokenType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByToken(String token);
    Optional<VerificationToken> findByCredentialAndTokenTypeAndUsedAtIsNull(Credential credential, TokenType tokenType);
}
