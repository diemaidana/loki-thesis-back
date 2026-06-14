package com.loki.tesis.auth.verification.service;

import com.loki.tesis.auth.credential.entity.Credential;
import com.loki.tesis.auth.credential.service.CredentialService;
import com.loki.tesis.auth.verification.verificationToken.enums.TokenType;
import com.loki.tesis.auth.verification.verificationToken.service.VerificationTokenService;
import com.loki.tesis.shared.email.dto.EmailMessage;
import com.loki.tesis.shared.email.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class EmailVerificationService {
    private final VerificationTokenService tokenService;
    private final EmailService emailService;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Transactional
    public void sendVerificationEmail(Credential credential) {
        String token = tokenService.generate(credential, TokenType.EMAIL_VERIFICATION);
        EmailMessage emailMessage = buildEmailMessage(credential, token);
        emailService.send(emailMessage);
    }

    @Transactional
    public void verifyEmail(String token){
        Credential credential = tokenService.consume(token, TokenType.EMAIL_VERIFICATION);
        credential.setEmailVerified(true);
    }

    private EmailMessage buildEmailMessage(Credential credential, String token){

        String to = credential.getEmail();
        String subject = "Verifica tu email en Loki";
        String templateName = "email/verification";

        Map<String, Object> templateVariables = Map.of(
                "firstName", credential.getUser().getFirstName(),
                "verificationUrl", frontendUrl+"/verify-email?token="+token,
                "expirationHours", 24
        );

        EmailMessage emailMessage = new EmailMessage(
                to,
                subject,
                templateName,
                templateVariables
        );

        return emailMessage;
    }
}
