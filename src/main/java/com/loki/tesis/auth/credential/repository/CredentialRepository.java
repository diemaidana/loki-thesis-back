package com.loki.tesis.auth.credential.repository;

import com.loki.tesis.auth.credential.entity.Credential;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CredentialRepository extends JpaRepository<Credential, Long> {
     Optional<Credential> findByEmail(String email);
     boolean existsByEmail(String email);
     Optional<Credential> findByUserId(Long userId);
}
