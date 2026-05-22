package com.seap.emailapi.dto;

public record AgentCallbackPayload(
        String contratoId,
        String emailId,
        String resposta
) {
}
