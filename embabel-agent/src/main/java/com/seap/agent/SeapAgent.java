package com.seap.agent;

import com.embabel.agent.api.annotation.AchievesGoal;
import com.embabel.agent.api.annotation.Action;
import com.embabel.agent.api.annotation.Agent;
import com.embabel.agent.api.common.Ai;
import com.embabel.agent.api.common.OperationContext;
import com.embabel.agent.api.tool.Tool;
import com.embabel.common.ai.model.LlmOptions;
import com.seap.ContractRepository;
import com.seap.domain.*;
import com.seap.domain.informacoes.*;
import com.seap.dto.AgentCallbackPayload;
import com.seap.dto.StarterAgentDTO;
import com.seap.personas.Personas;
import com.seap.tools.ApiTools;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Agent(
        name = "seap-agent",
        description = "agent used to fill the values to create "
)
public class SeapAgent {


    List<Tool> webTools = Tool.fromInstance(new ApiTools());

    List<Tool> tools = Tool.safelyFromInstance(this.webTools);

    @Action
    Enterprise getEnterprise(StarterAgentDTO documentName, Ai ai) throws InterruptedException {

        System.out.println("[INFORMATION]: " + documentName);

        var info = ai
                //.withAutoLlm()
                .withDefaultLlm()
                .withPromptContributor(Personas.ENTERPRISE_SEARCHER)
                .withToolObject(new ApiTools())
                .createObject("""    
                      Document name is: '%s'
                      You need to search on vector_search_api to return values about enterprises, like values on JSON Structure.
                      For this, format a simple text to call the Tool.
                      Just call the tool using simple values (clean text) like: empresa, nome, telefone, etc...
                      
                      Use the data searched to create this object
                      """.formatted(documentName.documentName()), Enterprise.class);
        System.out.println(" [RESULT - 01]: " + info);
        Thread.sleep(30000);
        return info;
    }
    

    @Action
    Vacancies getVacancies(StarterAgentDTO documentName, Ai ai) throws InterruptedException {

        System.out.println("[INFORMATION]: " + documentName);
        var info = ai
                .withDefaultLlm()
                .withPromptContributor(Personas.VACANCY_SEARCHER)
                .withToolObject(new ApiTools())
                .createObject("""
                      Document name is: '%s'
                      You need to search on vector_search_api to return values about vacancies, like values on JSON Structure.
                      For this, format a simple text to call the Tool.
                      Just call the tool using simple values (clean text) like: funcaoId, quantidade, vagas, etc...
                      Use all values received from search tool, the result of search can be 3 values, use all this data to search the vacancies, if have more than one
                      
                      Use the data searched to create this object
                      """.formatted(documentName.documentName()), Vacancies.class);

        System.out.println(" [RESULT - 02]: " + info);

        Thread.sleep(30000);

        return info;
    }



    @Action
    GeneralData getGeneralInformation(StarterAgentDTO documentName, Ai ai) throws InterruptedException {

        System.out.println("[INFORMATION]: " + documentName);

        var info = ai
                .withDefaultLlm()
                .withPromptContributor(Personas.INFORMATION_SEARCHER)
                .withToolObject(new ApiTools())
                .createObject("""
                      Document name is: '%s'
                      You need to search on vector_search_api to return the general values about the contract, like values on JSON Structure.
                      For this, format a simple text to call the Tool.
                      Just call the tool using simple values (clean text) like: orgaoId, data inicio vigencia, tipo de trabalho, etc...
                      
                      Use the data searched to create this object
                      """.formatted(documentName.documentName()), GeneralData.class);

        Thread.sleep(30000);
        return info;
    }


//    @Action
//    public GeneralData getGeneralData(GeneralInformation generalInformation, OperationContext context) throws InterruptedException {
//        var genData = context.ai()
//                .withLlm("gemini-2.5-flash-lite")
//                .createObject("""
//                        Create this object using this data:
//
//                Data:
//                '%s'
//                """.formatted(generalInformation.information()),  GeneralData.class);
//
//        Thread.sleep(30000);
//
//        return genData;
//    }

    @Action
    public ContractJSON genContractJson(Enterprise enterprise, Vacancies vacancies, GeneralData generalData, Ai ai) throws InterruptedException {
        var result = ai
                //.withLlm("gemini-2.5-flash")
                .withDefaultLlm()
                .withPromptContributor(Personas.SEARCHER)
                .withToolObject(new ApiTools())
                .createObject("""
                        Create a ContractJSon using a JSON structure and the values below
                        
                        [Enterprise]:
                        '%s'
                        
                        =======
                        [vacancies]:
                        '%s'
                        
                        ======
                        [generalData]:
                        '%s'
                        
                        For empty or null values, set to: Value Not Found
                        """.formatted(enterprise.toString(), vacancies.toString(), generalData.toString()), ContractJSON.class);
        System.out.println("Final Result: " + result);

        Thread.sleep(20000);

        return result;
    }


    @Action
    public MissingData reviewContract(ContractJSON contractJSON, StarterAgentDTO documentName, Ai ai) throws InterruptedException {

        Thread.sleep(30000);

        var missingData = ai
                .withDefaultLlm()
                .withPromptContributor(Personas.REVIEWER)
                .createObject("""
                    Analyze the following contract data: '%s'
                    Identify all fields specifically marked as 'Value Not Found'.
                    Return a structured MissingData object containing these fields.
                   
                    
                    """.formatted(contractJSON.toString()), MissingData.class);

        System.out.println(" [MISSING DATA IDENTIFIED]: " + missingData);

        if (!missingData.data().isBlank()) {

            System.out.println("LOG: Dados salvos e email enviado via Java Services.");
        }

        return missingData;
    }




    // parte pos resposta

    @Action
    public ContractJsonFromDB getContractJson(AgentCallbackPayload payload, Ai ai) throws InterruptedException {

        Thread.sleep(30000);

        var res = ai
                .withDefaultLlm()
                .withToolObject(new ApiTools())
                .createObject("""
                        Based on contract name '%s', get the current state of contract and create a ContractJson.object
                        
                        use the tool: database_get_contract_by_name
                        """.formatted(payload.contratoId()), ContractJsonFromDB.class);

        System.out.println("ContractJSON - from db: " + res);

        return res;

    }

    @Action
    public ContractJSON reviewerFromReply(ContractJsonFromDB contractJSON, AgentCallbackPayload payload, Ai ai) throws InterruptedException {

        Thread.sleep(30000);

        System.out.println("[reposta]: " + payload.resposta());

        var newContract = ai
                .withDefaultLlm()
                .withPromptContributor(Personas.EMAIL_REVIEWER)
                .withToolObject(new ApiTools())
                .createObject("""
                    Analyze the following contract data
                    Identify all fields specifically marked as 'Value Not Found'.
                    Try to replace this missing values with the values from email response
                    
                    Contract data: '%s'
                    
                    email response: '%s'
                    
                    """.formatted(contractJSON.toString(), payload.resposta()), ContractJSON.class);

        System.out.println(" [REVIEWED JSON]: " + newContract);

        if (!newContract.toString().isBlank()) {

            System.out.println("LOG[2]: Dados salvos e email enviado via Java Services.");
        }

        return newContract;
    }

    @Action
    public MissingData reviewContract2(ContractJSON contractJSON, Ai ai) throws InterruptedException {

        Thread.sleep(30000);

        var missingData = ai
                .withDefaultLlm()
                .withPromptContributor(Personas.REVIEWER)
                .createObject("""
                    Analyze the following contract data: '%s'
                    Identify all fields specifically marked as 'Value Not Found'.
                    Return a structured MissingData object containing these fields.
                   
                    
                    """.formatted(contractJSON.toString()), MissingData.class);

        System.out.println(" [MISSING DATA IDENTIFIED]: " + missingData);

        if (!missingData.data().isBlank()) {

            System.out.println("LOG: Dados salvos e email enviado via Java Services.");
        }

        return missingData;
    }




    @AchievesGoal(description = "Check the contract and search missing values")
    @Action
    public FinalStep checkToSend(ContractJSON contractJSON, MissingData missingData, Ai ai) throws InterruptedException {

        var resFinal = ai
                .withDefaultLlm()
                .withToolObject(new ApiTools())
                .createObject("""
                        Check the contract data. Identify if some field is marked as 'Value Not Found'.
                        if not, check if the missing data is null.
                        
                        if the missing data is null (no have fields described) and all fields on contractJson is filled, mark the field 'Complete' on this object to True.
                        IF not, mark to False
                        
                        ====================
                        values
                        
                        ContractJson: '%s'
                        
                        MissingData: '%s'
                        
                        Send a email using the Tool: email_call_api and use the missing data(like string) in email body and create a short subject for email based on data;
                        subject had a max size: 255 chars
                        
                        after this save the contract on database using a tool: database_save_contract, and set complete field based on this object complete field
                       
                        name of contract to save on database should be the same of field 'contractId' to send email
                        
                        """.formatted(contractJSON.toString(), missingData.data()), FinalStep.class);

        System.out.println("[FINAL STEP]: " + resFinal);
        return resFinal;

    }



}
