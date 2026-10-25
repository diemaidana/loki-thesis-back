package com.loki.tesis.shared.email.dto;

import java.util.Map;

public record EmailMessage(
        String to,
        String subject,
        String templateName,
        Map<String, Object> templateVariables
) {}
