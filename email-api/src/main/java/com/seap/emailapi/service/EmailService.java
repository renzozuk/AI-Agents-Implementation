package com.seap.emailapi.service;


import com.seap.emailapi.dto.AgentCallbackPayload;
import com.seap.emailapi.dto.EmailResponseDTO;
import com.seap.emailapi.dto.ReplyEmailRequest;
import com.seap.emailapi.dto.SendEmailRequest;
import com.seap.emailapi.model.EmailInteraction;
import com.seap.emailapi.repository.EmailRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class EmailService {

    private final EmailRepository repository;
    private final RestClient restClient;

    public EmailService(EmailRepository repository) {
        this.repository = repository;
        this.restClient = RestClient.create();
    }


    public EmailResponseDTO sendEmail(SendEmailRequest request) {
        String CALLBACK_URL = "http://localhost:8001/agent/callback";
        EmailInteraction email = new EmailInteraction(
                request.contratoId(),
                request.to(),
                request.subject(),
                request.body(),
                CALLBACK_URL
        );

        System.out.println(">>> [SIMULADOR] Enviando Email para: " + request.to());
        System.out.println(">>> Conteúdo: " + request.body());

       repository.save(email);

       return new EmailResponseDTO(
               email.getId(),
               email.getContratoId(),
               email.getAssunto(),
               email.getCorpoMensagem(),
               email.getDataEnvio(),
               email.getDataResposta()
       );
    }

    public void replyToEmail(UUID emailId, ReplyEmailRequest replyRequest) {
        EmailInteraction email = repository.findById(emailId)
                .orElseThrow(() -> new RuntimeException("Email não encontrado: " + emailId));

        if (email.isRespondido()) {
            throw new RuntimeException("Este email já foi respondido.");
        }

        // 2. Atualizar estado local
        email.setRespondido(true);
        email.setDataResposta(LocalDateTime.now());
        repository.save(email);

        // 3. Acordar o Agente (Webhook)
        System.out.println(">>> [SIMULADOR] Recebida resposta humana. Notificando agente em: " + email.getAgentCallbackUrl());

        AgentCallbackPayload payload = new AgentCallbackPayload(
                email.getContratoId(),
                email.getId().toString(),
                replyRequest.repostaTexto()
        );

        System.out.println(">>> [SIMULADOR] Resposta enviada: " + replyRequest.repostaTexto());


        try {
            var cost = restClient.post()
                    .uri(email.getAgentCallbackUrl())
                    .body(payload)
                    .retrieve()
                    .toBodilessEntity()
                    .getBody();

            System.out.println(">>> [COST]: \n" + cost);

            System.out.println(">>> [SIMULADOR] Agente notificado com sucesso.");


        } catch (Exception e) {
            System.err.println(">>> [ERRO] Falha ao notificar agente: " + e.getMessage());
        }
    }
}