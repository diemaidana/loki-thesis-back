package com.loki.tesis.auth.verification.verificationToken.service;

import com.loki.tesis.auth.credential.entity.Credential;
import com.loki.tesis.auth.verification.verificationToken.entity.VerificationToken;
import com.loki.tesis.auth.verification.verificationToken.enums.TokenType;
import com.loki.tesis.auth.verification.verificationToken.exception.InvalidTokenTypeException;
import com.loki.tesis.auth.verification.verificationToken.exception.TokenAlreadyUsedException;
import com.loki.tesis.auth.verification.verificationToken.exception.TokenExpiredException;
import com.loki.tesis.auth.verification.verificationToken.exception.TokenNotFoundException;
import com.loki.tesis.auth.verification.verificationToken.repository.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Transactional
public class VerificationTokenService {
    private final VerificationTokenRepository tokenRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    public String generate(Credential credential, TokenType tokenType){

        /* Invalido los tokens previos */
        tokenRepository.findByCredentialAndTokenTypeAndUsedAtIsNull(credential, tokenType)
                .ifPresent(prev -> prev.setUsedAt(Instant.now()));

        /* Generamos un token nuevo para cualquier TokenType. */
        byte[] token = new byte[32];
        secureRandom.nextBytes(token);

        String tokenString = Base64.getUrlEncoder().withoutPadding().encodeToString(token);
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(tokenString);
        verificationToken.setTokenType(tokenType);
        verificationToken.setCredential(credential);
        verificationToken.setExpiresAt(Instant.now().plus(24, ChronoUnit.HOURS));

        tokenRepository.save(verificationToken);
        return tokenString;
    }

    public Credential consume(String tokenString, TokenType expectedTokenType){
        VerificationToken token = tokenRepository.findByToken(tokenString)
            .orElseThrow(() -> new TokenNotFoundException("No se encontró el token."));
        if(token.getTokenType() != expectedTokenType){
            throw new InvalidTokenTypeException("Tipo de token incorrecto.");
        }
        if(token.getUsedAt() != null){
            throw new TokenAlreadyUsedException("El token ya fue utilizado.");
        }
        if(Instant.now().isAfter(token.getExpiresAt())){
            throw new TokenExpiredException("El token expiró.");
        }

        token.setUsedAt(Instant.now());
        return  token.getCredential();
    }
}
