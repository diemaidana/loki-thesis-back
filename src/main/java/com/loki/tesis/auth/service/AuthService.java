package com.loki.tesis.auth.service;

import com.loki.tesis.auth.credential.entity.Credential;
import com.loki.tesis.auth.credential.service.CredentialService;
import com.loki.tesis.auth.dto.response.AccountResponseDTO;
import com.loki.tesis.auth.dto.request.LoginRequestDTO;
import com.loki.tesis.auth.dto.response.LoginResponseDTO;
import com.loki.tesis.auth.dto.request.RegisterRequestDTO;
import com.loki.tesis.auth.exception.AccountLockedException;
import com.loki.tesis.auth.exception.InvalidCredentialsException;
import com.loki.tesis.auth.mapper.AuthMapper;
import com.loki.tesis.auth.verification.service.EmailVerificationService;
import com.loki.tesis.shared.security.service.JwtService;
import com.loki.tesis.user.entity.User;
import com.loki.tesis.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
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

    @Value("${app.auth.max-failed-attempts}")
    private int maxFailedAttempts;

    @Value("${app.auth.lockout-duration-minutes}")
    private int lockoutDurationMinutes;

    @Value("${app.auth.lock-notification-throttle-hours}")
    private int lockoutNotificationThrottleHours;

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
    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {

        Credential credential = credentialService
                .findByEmailOptional(loginRequestDTO.email())
                .orElseThrow(
                    () ->  new InvalidCredentialsException("Credenciales invalidas")
                );

        if(credential.getLockedUntil() != null && credential.getLockedUntil().isAfter(Instant.now())) {
            throw new AccountLockedException("Cuenta bloqueada debido a múltiples intentos fallidos. Intente nuevamente después de " + lockoutDurationMinutes + " minutos.");
        }

        if(credential.getLockedUntil() != null && credential.getLockedUntil().isBefore(Instant.now())) {
            credential.setLockedUntil(null);
            credential.setLoginAttempts(0);
        }

        if(passwordEncoder.matches(loginRequestDTO.password(), credential.getPassword())) {
            String token = jwtService.generateToken(credential.getUser(), credential.getEmail());
            String expiresAt = Instant.now().plus(jwtService.getJwtExpiration()).toString();
            AccountResponseDTO accountResponseDTO = authMapper.toAccountResponseDTO(credential.getUser(), credential);

            credential.setLoginAttempts(0);  // Lo seteamos 0 si las credenciales son correctas
            credential.setLockedUntil(null); // Lo seteamos null si las credenciales son correctas

            credentialService.updateForLogin(credential); // update a la credencial.

            return authMapper.toLoginResponseDTO(accountResponseDTO, token, expiresAt);
        } else {
            credential.setLoginAttempts(credential.getLoginAttempts() + 1);

            if(credential.getLoginAttempts() >= maxFailedAttempts) {
                Instant umbral = Instant.now().minus(Duration.ofHours(lockoutNotificationThrottleHours));

                if(credential.getLastLockNotificationAt() == null || credential.getLastLockNotificationAt().isBefore(umbral)) {
                    credential.setLastLockNotificationAt(Instant.now());
                    credentialService.updateForLogin(credential);
                    emailVerificationService.sendLockNotificationEmail(lockoutNotificationThrottleHours, credential);
                }
                else {
                    credentialService.updateForLogin(credential);
                }

                credential.setLockedUntil(Instant.now().plusSeconds(lockoutDurationMinutes * 60L));
                throw new AccountLockedException("Cuenta bloqueada debido a múltiples intentos fallidos. Intente nuevamente después de " + lockoutDurationMinutes + " minutos.");
            }
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
