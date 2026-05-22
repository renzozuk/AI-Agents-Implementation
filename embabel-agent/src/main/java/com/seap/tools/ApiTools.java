package com.seap.tools;

import com.embabel.agent.api.annotation.LlmTool;
import com.embabel.agent.api.tool.Tool;
import com.seap.ContractRepository;
import com.seap.domain.ContractJSON;
import com.seap.dto.EmailResponseDTO;
import com.seap.dto.SearchResult;
import com.seap.model.Contract;
import jakarta.persistence.EntityNotFoundException;
import jdk.dynalink.linker.LinkerServices;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Optional;

@Component
public class ApiTools {

    // Pode ser uma classe interna ou arquivo separado
    public record EmailRequestDTO(String to, String subject, String body, String contratoId) {}

    private final RestClient restClient;


    public ApiTools() {
        this.restClient = RestClient.create();
    }


    @LlmTool(name = "vector_search_api", description = "get information's from vector database using a external api, using the documentName and search values")
    public List<SearchResult> searchTool(
            @LlmTool.Param(description = "information used to get the values from vector database api external") String query,
            @LlmTool.Param(description = "name by document for use in search (documentName)") String documentName) {
        try {
            List<SearchResult> results = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("http")
                            .host("localhost")
                            .port(8282)
                            .path("/api/search")
                            .queryParam("documentName", documentName)
                            .queryParam("query", query)
                            .build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<SearchResult>>() {});

            if (results != null && !results.isEmpty()) {
                return results;
            }

            return null;

        } catch (Exception e) {
            System.err.println(">>> [ERRO] busca no vector deu erro: " + e.getMessage());
            return null;
        }
    }

    @LlmTool(name = "email_call_api", description = "send a email message to start/or continues a conversation by messages")
    public EmailResponseDTO emailStarterTool(
            @LlmTool.Param(description = "id used to reference the contract that is being processed") String contractId,
            @LlmTool.Param(description = "the email address of the message receiver") String to,
            @LlmTool.Param(description = "the subject of the message, the reason why this message is being sent (should be short and direct).") String subject,
            @LlmTool.Param(description = "the body of the message, everything that is said to the receiver is written here so that they can read and respond.") String body
            ) {

        System.out.println("[SEND - EMAIL: ");
        System.out.println(contractId);
        System.out.println(to);
        System.out.println(subject);
        System.out.println(body);

        EmailRequestDTO requestBody = new EmailRequestDTO(to, subject, body, contractId);

        try {
            return restClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("http")
                            .host("localhost")
                            .port(9090)
                            .path("/api/emails/send")
                            .build())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(EmailResponseDTO.class);
        }
        catch (Exception e) {
            System.err.println(">>> [ERRO] erro ao enviar email: " + e.getMessage());
            return null;
        }
    }

    @LlmTool(name = "database_save_contract", description = "used to save contracts on database, like a Contract.class")
    public Contract saveContract(
            @LlmTool.Param(description = "The title or display name of the contract. It should be descriptive enough to identify the document later (e.g., 'Service Agreement - Acme Corp').") String name,
            @LlmTool.Param(description = "The Contract.class object created and reviewed") String contract,
            @LlmTool.Param(description = "A textual description detailing any mandatory information that is currently missing or pending to finalize the contract. If all necessary data is present, this should be null or an empty string.") String missingData,
            @LlmTool.Param(description = "The valid email address of the person responsible for creating or validating this contract.") String email_resp,
            @LlmTool.Param(description = "Value used to knew if the contract is process is fully completed") String complete

    ) {

        System.out.println( "[DADOS save_contract:]");
        System.out.println( name );
        System.out.println(missingData);
        System.out.println( email_resp );

        System.out.println("[CONTRATO JSON: " + contract);



        ContractRepository contractRepository = SpringContext.getBean(ContractRepository.class);

        Optional<Contract> contractSearch = contractRepository.findByName(name);

        Contract contractEntity;

        if (contractSearch.isPresent()) {
            contractEntity = contractSearch.get();
            contractEntity.setFinalJson(contract);
            contractEntity.setMissingData(missingData);
            contractEntity.setEmailResp(email_resp);
            contractEntity.setComplete(complete);
        } else {
            contractEntity = new Contract(
                    name,
                    contract,
                    missingData,
                    email_resp,
                    complete
            );
        }

        return contractRepository.save(contractEntity);






    }

    @LlmTool(name = "database_update_contract", description = "used to update contracts on database, like a Contract.class")
    public Contract updateContract(
            @LlmTool.Param(description = "ID for contract used to search on database for update") Long id,
            @LlmTool.Param(required = false, description = "The Contract.class object created and reviewed for update, if will be changed") String contractJson,
            @LlmTool.Param(required = false, description = "A textual description detailing any mandatory information that is currently missing or pending to finalize the contract. If all necessary data is present, this should be null or an empty string.") String missingData,
            @LlmTool.Param(description = "Value used to knew if the contract is process is fully completed") String complete

    ) {
        ContractRepository contractRepository = SpringContext.getBean(ContractRepository.class);

        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Contract cannot be found with id: " + id));

        if (StringUtils.hasText(contractJson)) {
            contract.setFinalJson(contractJson);
        }

        if (missingData != null && !missingData.isBlank()) {
            contract.setMissingData(missingData);
        }

        if (complete != null && !complete.isBlank()) {
            contract.setComplete(complete);
        }

        return contractRepository.save(contract);
    }

    @LlmTool(name = "database_get_contract_by_name", description = "used to get contracts on database using contract name, like a Contract.class")
    public Contract getContract(
            @LlmTool.Param(description = "NAME for contract used to search on database for update") String name
    ) {
        ContractRepository contractRepository = SpringContext.getBean(ContractRepository.class);

        var contract = (Contract) contractRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("Contract cannot be found with id: " + name));

        System.out.println("[CONTRATO BUSCADO NO DATABASE]:" + contract);

        return contract;


    }




}
