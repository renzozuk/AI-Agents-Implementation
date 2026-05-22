package com.seap.controller;

import com.embabel.agent.core.Agent;
import com.embabel.agent.core.AgentPlatform;
import com.embabel.agent.core.AgentProcess;
import com.embabel.agent.core.ProcessOptions;
import com.seap.ProcessingValues;
import com.seap.dto.AgentCallbackPayload;
import com.seap.dto.StarterAgentDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/*
* curl --location 'http://localhost:8001/agent/newDocument' \
--header 'Content-Type: application/json' \
--data '{
    "documentName": "ContratoES.pdf"
}'
*
*
*
- Complemento do endereço
- Observações da empresa
- ID do Órgão
- Data de Início da Vigência
- Data de Fim da Vigência
- Tipo de Trabalho
- ID da Função para Vaga 1
*
*
*
* chamada para reply:
curl --location 'http://localhost:9090/api/emails/a7a96185-73ef-4ed5-a774-35c6a5971bb5/reply' --header 'Content-Type: application/json' --data '{
    "respostaTexto": "Empresa.Complemento=Empresa; Empresa.Observacoes=Empresa que ira contratar os detentos; OrgaoId; 0987; dataInicio=01/01/2020; dataFim=31/12/2022; tipoTrabalho=trabalho remunerado para resocializacao; Id da funcao relacionda=0123"
}'
*
* curl -X POST "http://localhost:9090/api/emails/128eb60a-54d2-47c1-b4cc-bcc96ad15e8d/reply" \
     -H "Content-Type: application/json" \
     -d '{
           "repostaTexto": "
empresa.endereco.complemento=empresa, data.valorCentavosContrato=200000000, data.dataInicioVigencia=01/01/2020, data.dataFimVigencia=31/12/2022, data.localTrabalho=sesap-natal/rn, data.tipoTrabalho=trabalho manual, data.cargaHoraria=8h por dia, vagas[0].funcaoId=0123"
         }'

*
* */

@RestController
@RequestMapping("/agent")
public class AgentController {

    private final AgentPlatform agentPlatform;

    private final String AGENT_NAME = "seap-agent";
    private final String REPLY_AGENT_NAME = "reply-agent";

    public AgentController(AgentPlatform agentPlatform) {
        this.agentPlatform = agentPlatform;
    }

    @PostMapping("/newDocument")
    public ResponseEntity<?> starByNewDocument(@RequestBody StarterAgentDTO documentName){
        System.out.println("Novo contrato recebido com id: " + documentName.documentName());

        Agent agent = agentPlatform.agents().stream()
                .filter(a -> a.getName().toLowerCase().contains(AGENT_NAME))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Agente nao encontrado"));

        AgentProcess agentProcess = agentPlatform.createAgentProcessFrom(
                agent,
                ProcessOptions.DEFAULT,
                documentName
        );

        new ProcessingValues(
                agentProcess,
                "Start agent to process new document",
                documentName.documentName()
        );

        agentProcess.run();


        System.out.println(" [COST - FLUX 1]: " + agentProcess.costInfoString(true));

        return ResponseEntity.ok().body(agentProcess.costInfoString(true));

    }


    @PostMapping("/callback")
    public String callback(@RequestBody AgentCallbackPayload payload, Model model) {
        System.out.println("Resposta recebida do email para o contrato: " + payload.contratoId());

        Agent agent = agentPlatform.agents().stream()
                .filter(a -> a.getName().toLowerCase().contains(AGENT_NAME))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Agente nao encontrado"));

        AgentProcess agentProcess = agentPlatform.createAgentProcessFrom(
                agent,
                ProcessOptions.DEFAULT,
                payload
        );

        new ProcessingValues(
                agentProcess,
                "Update JSON using email response",
                payload.resposta()
        ).addToModel(model);

        agentProcess.run();

        System.out.println(" [COST - FLUX 2]: " + agentProcess.costInfoString(true));


        return agentProcess.costInfoString(true);
    }
}
