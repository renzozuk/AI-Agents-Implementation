package com.seap.emailapi.controller;


import com.seap.emailapi.dto.EmailResponseDTO;
import com.seap.emailapi.dto.ReplyEmailRequest;
import com.seap.emailapi.dto.SendEmailRequest;
import com.seap.emailapi.model.EmailInteraction;
import com.seap.emailapi.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/emails")
public class EmailController {

    private final EmailService service;

    public EmailController(EmailService service) {
        this.service = service;
    }

    // O AGENTE chama isso
    @PostMapping("/send")
    public ResponseEntity<EmailResponseDTO> sendEmail(@RequestBody SendEmailRequest request) {
        System.out.println(">>> [SEND] request: " + request);
        return ResponseEntity.ok(service.sendEmail(request));
    }

    // VOCÊ (Humano) chama isso para responder
    @PostMapping("/{emailId}/reply")
    public ResponseEntity<Void> reply(@PathVariable UUID emailId, @RequestBody ReplyEmailRequest request) {
        service.replyToEmail(emailId, request);
        return ResponseEntity.ok().build();
    }
}