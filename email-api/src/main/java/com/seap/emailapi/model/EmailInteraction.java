package com.seap.emailapi.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
public class EmailInteraction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String contratoId;
    private String destinatario;
    private String assunto;
    private String corpoMensagem;

    // URL para onde enviaremos o POST após resposta
    private String agentCallbackUrl;

    private boolean respondido;
    private LocalDateTime dataEnvio;
    private LocalDateTime dataResposta;

    public EmailInteraction(String contratoId, String destinatario, String assunto, String corpoMensagem, String agentCallbackUrl) {
        this.contratoId = contratoId;
        this.destinatario = destinatario;
        this.assunto = assunto;
        this.corpoMensagem = corpoMensagem;
        this.agentCallbackUrl = agentCallbackUrl;
        this.respondido = false;
        this.dataEnvio = LocalDateTime.now();
    }
}