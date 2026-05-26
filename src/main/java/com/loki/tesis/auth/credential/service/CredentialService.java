package com.loki.tesis.auth.credential.service;

import com.loki.tesis.auth.credential.entity.Credential;
import com.loki.tesis.auth.credential.repository.CredentialRepository;
import com.loki.tesis.auth.exception.CredentialNotFoundException;
import com.loki.tesis.auth.exception.EmailAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CredentialService {
    private final CredentialRepository credentialRepository;

    @Transactional
    public Credential save(Credential credential) {
        validateEmailIsUnique(credential.getEmail());
        return credentialRepository.save(credential);
    }

    public Credential findByEmail(String email) {
        return  credentialRepository.findByEmail(email)
                .orElseThrow(() -> new CredentialNotFoundException("No se encontro ningun email: " + email));
    }

    // Funciones privadas o de uso interno.
    private void validateEmailIsUnique(String email) {
        if(credentialRepository.existsByEmail(email)){
            throw new EmailAlreadyExistsException("El email ya esta registrado");
        }
    }

}
