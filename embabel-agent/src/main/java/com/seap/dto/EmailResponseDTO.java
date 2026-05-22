package com.seap.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record EmailResponseDTO (
        UUID id,
        String contratoId,
        String assunto,
        String corpoMensagem,
        LocalDateTime dataEnvio,
        LocalDateTime dataResposta
) {}