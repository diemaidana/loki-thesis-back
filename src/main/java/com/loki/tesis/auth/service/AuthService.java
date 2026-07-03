package com.loki.tesis.auth.service;

import com.loki.tesis.auth.credential.entity.Credential;
import com.loki.tesis.auth.credential.enums.RoleType;
import com.loki.tesis.auth.credential.service.CredentialService;
import com.loki.tesis.auth.dto.request.LoginRequestDTO;
import com.loki.tesis.auth.dto.request.RegisterRequestDTO;
import com.loki.tesis.auth.dto.response.AccountResponseDTO;
import com.loki.tesis.auth.dto.response.LoginResponseDTO;
import com.loki.tesis.auth.exception.AccountLockedException;
import com.loki.tesis.auth.exception.InvalidCredentialsException;
import com.loki.tesis.auth.mapper.AuthMapper;
import com.loki.tesis.auth.verification.service.EmailVerificationService;
import com.loki.tesis.shared.security.service.JwtService;
import com.loki.tesis.user.entity.User;
import com.loki.tesis.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
    private static final String INVALID_CREDENTIALS_MESSAGE = "Credenciales invalidas";

    private final UserService userService;
    private final CredentialService credentialService;
    private final AuthenticationManager authenticationManager;
    private final AuthMapper authMapper;
    private final EmailVerificationService emailVerificationService;
    private final JwtService jwtService;

    @Value("${app.auth.max-failed-attempts}")
    private int maxFailedAttempts;

    @Value("${app.auth.lockout-duration-minutes}")
    private int lockoutDurationMinutes;

    @Value("${app.auth.lock-notification-throttle-hours}")
    private int lockoutNotificationThrottleHours;

    @Transactional(readOnly = true)
    public AccountResponseDTO getCurrentUser(Credential credential) {
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

    @Transactional(noRollbackFor = {InvalidCredentialsException.class, AccountLockedException.class})
    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        Credential credential;

        try{
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequestDTO.email(),
                            loginRequestDTO.password()
                    )
            );
            credential = (Credential) authentication.getPrincipal();

            return handleSuccessfulLogin(credential);
        }
        catch(LockedException e){
            throw new AccountLockedException(accountLockedMessage());
        }
        catch (DisabledException e){
            throw new InvalidCredentialsException(INVALID_CREDENTIALS_MESSAGE);
        }
        catch (BadCredentialsException e){
            handleFailedLoginByEmail(loginRequestDTO.email());
            throw new InvalidCredentialsException(INVALID_CREDENTIALS_MESSAGE);
        }
    }


    private LoginResponseDTO handleSuccessfulLogin(Credential credential) {
        credential.setLoginAttempts(0);
        credential.setLockedUntil(null);

        String uuid = credential.getUser().getUuid().toString();
        String email = credential.getEmail();
        RoleType role = credential.getRoleType();

        String token = jwtService.generateToken(uuid, email, role);
        String expiresAt = Instant.now().plus(jwtService.getJwtExpiration()).toString();

        AccountResponseDTO account = authMapper.toAccountResponseDTO(credential.getUser(), credential);
        return authMapper.toLoginResponseDTO(account, token, expiresAt);
    }

    private void handleFailedLoginByEmail(String email) {
        Optional<Credential> opt = credentialService.findByEmailOptional(email);

        if (opt.isEmpty()) {
           return;
        }

        Credential credential = opt.get();

        if(credential.getLockedUntil() != null && credential.getLockedUntil().isBefore(Instant.now())) {
            credential.setLoginAttempts(0);
            credential.setLockedUntil(null);
        }

        credential.setLoginAttempts(credential.getLoginAttempts() + 1);

        if (credential.getLoginAttempts() >= maxFailedAttempts) {
            credential.setLockedUntil(Instant.now().plus(Duration.ofMinutes(lockoutDurationMinutes)));
            notifyAccountLockedIfNotThrottled(credential);
            throw new AccountLockedException(accountLockedMessage());
        }
    }

    private void notifyAccountLockedIfNotThrottled(Credential credential) {
        Instant throttleThreshold = Instant.now().minus(Duration.ofHours(lockoutNotificationThrottleHours));
        Instant lastNotification = credential.getLastLockNotificationAt();

        if (lastNotification != null && lastNotification.isAfter(throttleThreshold)) {
            return;
        }
        credential.setLastLockNotificationAt(Instant.now());
        // emailVerificationService.sendLockNotificationEmail(lockoutDurationMinutes, credential);
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
