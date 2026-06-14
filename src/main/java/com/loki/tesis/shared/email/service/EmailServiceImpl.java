package com.loki.tesis.shared.email.service;

import com.loki.tesis.shared.email.dto.EmailMessage;
import com.loki.tesis.shared.email.exception.EmailSendException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.UnsupportedEncodingException;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender; // bean de Spring
    private final SpringTemplateEngine templateEngine; // Bean de Thymeleaf

    @Value("${app.mail.from}")
    private String fromEmail;

    @Value("${app.mail.from-name}")
    private String fromName;

    @Async
    @Override
    public void send(EmailMessage emailMessage) {
        Context context = new Context();
        context.setVariables(emailMessage.templateVariables()); // Usamos estas dos para poner en contexto las variables de Thymeleaf

        String htmlContent = templateEngine.process(emailMessage.templateName(), context); // Esto sustituye todas las ${variables} por el contenido en templateName
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom(fromEmail, fromName);
            helper.setTo(emailMessage.to());
            helper.setSubject(emailMessage.subject());
            helper.setText(htmlContent, true);
            mailSender.send(mimeMessage);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new EmailSendException("Error al enviar email: "+ emailMessage.to(), e);
        }
    }
}
