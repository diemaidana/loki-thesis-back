package com.loki.tesis.shared.security.service;

import com.loki.tesis.auth.credential.entity.Credential;
import com.loki.tesis.auth.credential.repository.CredentialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CredentialUserDetailsService implements UserDetailsService {
    private final CredentialRepository  credentialRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return credentialRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
    }

    @Transactional(readOnly = true)
    public Credential loadUserByUuid(UUID uuid) throws UsernameNotFoundException {
        return credentialRepository.findByUser_Uuid(uuid)
                .orElseThrow(() -> new UsernameNotFoundException("Credential not found for uuid: " + uuid));
    }
}
