package com.loki.tesis.auth.service;

import com.loki.tesis.auth.credential.entity.Credential;
import com.loki.tesis.auth.credential.service.CredentialService;
import com.loki.tesis.auth.dto.AccountResponseDTO;
import com.loki.tesis.auth.dto.RegisterRequestDTO;
import com.loki.tesis.auth.mapper.AuthMapper;
import com.loki.tesis.auth.verification.service.EmailVerificationService;
import com.loki.tesis.user.entity.User;
import com.loki.tesis.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
    private final UserService userService;
    private final CredentialService credentialService;
    private final AuthMapper authMapper;
    private final EmailVerificationService emailVerificationService;

    @Transactional(readOnly = true)
    public AccountResponseDTO getCurrentUser(String email) {
        Credential credential = credentialService.findByEmail(email);
        User user = credential.getUser();
        return authMapper.toAccountResponseDTO(user, credential);
    }

    @Transactional
    public AccountResponseDTO register(RegisterRequestDTO registerRequestDTO) {

        User user = userService.createUser(authMapper.toUserEntity(registerRequestDTO));

        Credential credential = authMapper.toCredentialEntity(registerRequestDTO);
        credential.setUser(user);
        Credential saved = credentialService.save(credential);

        // emailVerificationService.sendVerificationEmail(saved);

        return authMapper.toAccountResponseDTO(user, saved);
    }

    @Transactional
    public void verifyEmail(String token) {
        emailVerificationService.verifyEmail(token);
    }

    @Transactional
    public void resendEmailVerification(String email) {
        credentialService.findByEmailOptional(email)
                         .filter(c -> !c.isEmailVerified())
                         .ifPresent(emailVerificationService::sendVerificationEmail);
    }
}
