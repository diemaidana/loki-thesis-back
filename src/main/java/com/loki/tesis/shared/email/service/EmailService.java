package com.loki.tesis.shared.email.service;

import com.loki.tesis.shared.email.dto.EmailMessage;

public interface EmailService {
    void send(EmailMessage emailMessage);
}
