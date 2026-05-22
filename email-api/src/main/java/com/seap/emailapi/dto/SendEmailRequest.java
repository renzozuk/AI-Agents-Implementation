package com.seap.emailapi.dto;

public record SendEmailRequest(
        String contratoId,
        String to,
        String subject,
        String body
) {
}
