package com.loki.tesis.auth.credential.service;

import com.loki.tesis.auth.credential.entity.Credential;
import com.loki.tesis.auth.credential.repository.CredentialRepository;
import com.loki.tesis.auth.exception.CredentialNotFoundException;
import com.loki.tesis.auth.exception.EmailAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CredentialService {
    private final CredentialRepository credentialRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Credential save(Credential credential) {
        validateEmailIsUnique(credential.getEmail());

        credential.setPassword(passwordEncoder.encode(credential.getPassword()));

        return credentialRepository.save(credential);
    }

    @Transactional
    public Credential updateForLogin(Credential credential) {
        credentialRepository.save(credential);
        return credential;
    }
    public Credential findByEmail(String email) {
        return  credentialRepository.findByEmail(email)
                .orElseThrow(() -> new CredentialNotFoundException("No se encontro ningun email: " + email));
    }

    public boolean isEmailVerifiedForUser(Long id) {
        Credential credential = credentialRepository.findByUser_Id(id).orElseThrow(()  -> new CredentialNotFoundException("No se encontro ningun email verificado"));
        return credential.isEmailVerified();
    }

    // Funciones privadas o de uso interno.
    private void validateEmailIsUnique(String email) {
        if(credentialRepository.existsByEmail(email)){
            throw new EmailAlreadyExistsException("El email ya esta registrado");
        }
    }

    @Transactional(readOnly = true)
    public Optional<Credential> findByEmailOptional(String email) {
        return credentialRepository.findByEmail(email);
    }

}
