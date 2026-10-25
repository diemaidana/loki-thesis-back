package com.loki.tesis.auth.service;

import com.loki.tesis.auth.credential.entity.Credential;
import com.loki.tesis.auth.verification.verificationToken.enums.TokenType;
import com.loki.tesis.auth.verification.verificationToken.service.VerificationTokenService;
import com.loki.tesis.shared.email.dto.EmailMessage;
import com.loki.tesis.shared.email.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthEmailService {
    private final VerificationTokenService verificationTokenService;
    private final EmailService  emailService;
    private final PasswordEncoder passwordEncoder;


    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Value("${app.auth.password-reset-token-expiration-hours}")
    private long passwordResetTokenExpirationHours;

    @Value("${app.auth.email-verification-token-expiration-hours}")
    private long emailVerificationTokenExpirationHours;

    // Metodos publicos para cada email
    public void sendVerificationEmail(Credential credential) {
        String token = verificationTokenService.generate(credential, TokenType.EMAIL_VERIFICATION, emailVerificationTokenExpirationHours);
        String url = frontendUrl + "/verify-email?token=" + token;
        Map<String, Object> map = Map.of(
                "firstName", credential.getUser().getFirstName(),
                "verificationUrl", url,
                "expirationHours", emailVerificationTokenExpirationHours
        );

        emailService.send(buildEmailMessage(credential, "Verifica tu email en Loki", "email/verify-email", map));
    }

    public void sendLockNotificationEmail(Credential credential, int lockoutMinutes) {
        String url = frontendUrl + "/forgot-password";
        Map<String, Object> map = Map.of(
                "firstName", credential.getUser().getFirstName(),
                "lockoutMinutes", lockoutMinutes,
                "forgotPasswordUrl", url
        );
        emailService.send(buildEmailMessage(credential, "Alerta: Cuenta bloqueada por multiples intentos fallidos de inicio de sesión.", "email/account-locked", map));
    }

    public void sendPasswordResetEmail(Credential credential) {
        String token = verificationTokenService.generate(credential, TokenType.PASSWORD_RESET, passwordResetTokenExpirationHours);
        String url = frontendUrl + "/reset-password?token=" + token;
        Map<String, Object> map = Map.of(
                "firstName", credential.getUser().getFirstName(),
                "resetUrl", url,
                "expirationHours", passwordResetTokenExpirationHours
        );

        emailService.send(buildEmailMessage(credential, "Restablecé tu contraseña de Loki", "email/password-forgot", map));
    }

    public void sendPasswordChangeNotificationEmail(Credential credential) {
        Map<String, Object> map = Map.of(
                "firstName", credential.getUser().getFirstName()
        );
        emailService.send(buildEmailMessage(credential, "Tu contraseña fue actualizada.", "email/password-change", map));
    }

    // Metodo privado para la generación de emails.
    private EmailMessage buildEmailMessage(Credential credential, String subject, String templateName, Map<String, Object> variables) {
        return new EmailMessage(
                credential.getEmail(),
                subject,
                templateName,
                variables
        );
    }


    // Metodos de verificación de email y funciones parecidas.

    @Transactional
    public void verifyEmail(String token) {
        Credential credential = verificationTokenService.consume(token,  TokenType.EMAIL_VERIFICATION);
        credential.setEmailVerified(true);
    }

    @Transactional
    public void resetPassword(String token, String newPassword){
        Credential credential = verificationTokenService.consume(token,  TokenType.PASSWORD_RESET);
        credential.setPassword(passwordEncoder.encode(newPassword));
        credential.setTokenVersion(credential.getTokenVersion() + 1);
        sendPasswordChangeNotificationEmail(credential);
    }
}
