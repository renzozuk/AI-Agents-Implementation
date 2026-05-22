package com.seap.dto;

public record AgentCallbackPayload(
        String contratoId,
        String emailId,
        String resposta
) {
}
