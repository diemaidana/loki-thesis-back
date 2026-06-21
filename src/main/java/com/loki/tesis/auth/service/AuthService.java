package com.loki.tesis.auth.service;

import com.loki.tesis.auth.credential.entity.Credential;
import com.loki.tesis.auth.credential.service.CredentialService;
import com.loki.tesis.auth.dto.response.AccountResponseDTO;
import com.loki.tesis.auth.dto.request.LoginRequestDTO;
import com.loki.tesis.auth.dto.response.LoginResponseDTO;
import com.loki.tesis.auth.dto.request.RegisterRequestDTO;
import com.loki.tesis.auth.exception.InvalidCredentialsException;
import com.loki.tesis.auth.mapper.AuthMapper;
import com.loki.tesis.auth.verification.service.EmailVerificationService;
import com.loki.tesis.shared.security.service.JwtService;
import com.loki.tesis.user.entity.User;
import com.loki.tesis.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
    private final UserService userService;
    private final CredentialService credentialService;
    private final AuthMapper authMapper;
    private final EmailVerificationService emailVerificationService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

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

    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        Credential credential = credentialService
                .findByEmailOptional(loginRequestDTO.email())
                .orElseThrow(
                    () -> new InvalidCredentialsException("Credenciales invalidas")
                );

        if(passwordEncoder.matches(loginRequestDTO.password(), credential.getPassword())){
            String token = jwtService.generateToken(credential.getUser(), credential.getEmail());
            String expiresAt = Instant.now().plus(jwtService.getJwtExpiration()).toString();
            AccountResponseDTO accountResponseDTO = authMapper.toAccountResponseDTO(credential.getUser(), credential);

            return authMapper.toLoginResponseDTO(accountResponseDTO, token, expiresAt);
        } else {
            throw new InvalidCredentialsException("Credenciales invalidas");
        }

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
