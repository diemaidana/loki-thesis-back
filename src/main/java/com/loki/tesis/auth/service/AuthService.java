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
    private static final String INVALID_CREDENTIALS_MESSAGE = "Credenciales invalidas";

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

    // noRollbackFor: dejamos commitear los cambios sobre `loginAttempts`, `lockedUntil`
    // y `lastLockNotificationAt` aunque el método termine lanzando excepción.
    @Transactional(noRollbackFor = {InvalidCredentialsException.class, AccountLockedException.class})
    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        Credential credential = credentialService
                .findByEmailOptional(loginRequestDTO.email())
                .orElseThrow(() -> new InvalidCredentialsException(INVALID_CREDENTIALS_MESSAGE));

        ensureAccountNotLocked(credential);

        if (passwordEncoder.matches(loginRequestDTO.password(), credential.getPassword())) {
            return handleSuccessfulLogin(credential);
        }

        handleFailedLogin(credential);
        throw new InvalidCredentialsException(INVALID_CREDENTIALS_MESSAGE);
    }

    private void ensureAccountNotLocked(Credential credential) {
        Instant lockedUntil = credential.getLockedUntil();
        if (lockedUntil == null) {
            return;
        }
        if (lockedUntil.isAfter(Instant.now())) {
            throw new AccountLockedException(accountLockedMessage());
        }
        // El lockout ya expiró: limpiamos el estado para empezar de cero.
        credential.setLockedUntil(null);
        credential.setLoginAttempts(0);
    }

    private LoginResponseDTO handleSuccessfulLogin(Credential credential) {
        credential.setLoginAttempts(0);
        credential.setLockedUntil(null);

        String token = jwtService.generateToken(credential.getUser(), credential.getEmail());
        String expiresAt = Instant.now().plus(jwtService.getJwtExpiration()).toString();
        AccountResponseDTO account = authMapper.toAccountResponseDTO(credential.getUser(), credential);
        return authMapper.toLoginResponseDTO(account, token, expiresAt);
    }

    private void handleFailedLogin(Credential credential) {
        credential.setLoginAttempts(credential.getLoginAttempts() + 1);

        if (credential.getLoginAttempts() < maxFailedAttempts) {
            return;
        }

        credential.setLockedUntil(Instant.now().plus(Duration.ofMinutes(lockoutDurationMinutes)));
        notifyAccountLockedIfNotThrottled(credential);
        throw new AccountLockedException(accountLockedMessage());
    }

    private void notifyAccountLockedIfNotThrottled(Credential credential) {
        Instant throttleThreshold = Instant.now().minus(Duration.ofHours(lockoutNotificationThrottleHours));
        Instant lastNotification = credential.getLastLockNotificationAt();

        if (lastNotification != null && lastNotification.isAfter(throttleThreshold)) {
            return;
        }
        credential.setLastLockNotificationAt(Instant.now());
        emailVerificationService.sendLockNotificationEmail(lockoutDurationMinutes, credential);
    }

    private String accountLockedMessage() {
        return "Cuenta bloqueada debido a múltiples intentos fallidos. Intente nuevamente después de "
                + lockoutDurationMinutes + " minutos.";
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
