package com.seap;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class AgentControllerOLD {
//
//    private final ApiCallInterface apiCall;
//
//    private final AgentPlatform agentPlatform;
//
//    public AgentController(ApiCallInterface apiCall, AgentPlatform agentPlatform) {
//        this.apiCall = apiCall;
//        this.agentPlatform = agentPlatform;
//    }
//
//    @PostMapping("/updateEmail")
//    public String updateEmail(@RequestBody EmailResponse email, Model model) {
//        Agent agent = agentPlatform.agents().stream()
//                .filter(a -> a.getName().toLowerCase().contains("update"))
//                .findFirst()
//                .orElseThrow(() -> new RuntimeException("no agent found"));
//
//        AgentProcess agentProcess = agentPlatform.createAgentProcessFrom(
//                agent,
//                ProcessOptions.DEFAULT,
//                email
//        );
//
//        model.addAttribute("EmailResponse", email);
//
//        new GenericProcessingValues(
//                agentProcess,
//                "Update contract with email answer",
//                email.body(),
//                "FinalCheck",
//                "contrato"
//        ).addToModel(model);
//
//        agentProcess.run();
//
//        return agentProcess.toString();
//    }
//
//    @PostMapping("/plan")
//    public String plan(Model model) {
//        String call = "Get the contract based on this pdf: " + apiCall.get();
//
//        System.out.println("call: " + call);
//
//        UserInput input =  new UserInput(call);
//
//        Agent agent = agentPlatform.agents().stream()
//                .filter(a -> a.getName().toLowerCase().contains("agent"))
//                .findFirst()
//                .orElseThrow(() -> new RuntimeException("Agent not found"));
//
//        AgentProcess agentProcess = agentPlatform.createAgentProcessFrom(
//                agent,
//                ProcessOptions.DEFAULT,
//                call, input
//        );
//
//        model.addAttribute("callToAgent", call);
//        new GenericProcessingValues(
//                agentProcess,
//                "Searching your contract",
//                call,
//                "contrato",
//                "contrato"
//        ).addToModel(model);
//
//          agentProcess.run();
//
//
//        return agentProcess.toString();
//
//    }
//
//    @PostMapping("/callback")
//    public ResponseEntity<String> handleEmailCallback(@RequestBody EmailCallbackRequestDTO callback, Model model) {
//        System.out.println("Recebi resposta do e-mail:");
//        System.out.println("ThreadId: " + callback.getThreadId());
//        System.out.println("Mensagem: " + callback.getMessage());
//        System.out.println("RelactedId: " + callback.getRelatedContractId());
//
//        Agent agent = agentPlatform.agents().stream()
//                .filter(a -> a.getName().toLowerCase().contains("update"))
//                .findFirst()
//                .orElseThrow(() -> new RuntimeException("no agent found"));
//
//        UserInput input = new UserInput(callback.toString());
//
//        AgentProcess agentProcess = agentPlatform.createAgentProcessFrom(
//                agent,
//                ProcessOptions.DEFAULT,
//                callback
//        );
//
//        model.addAttribute("EmailCallback", callback);
//
//        new GenericProcessingValues(
//                agentProcess,
//                "Update contract with email answer",
//                 callback.getMessage(),
//                "FinalCheck",
//                "contrato"
//        ).addToModel(model);
//
//        agentProcess.run();
//
//
//
//        // Aqui você "acorda" o agente, usando o threadId + mensagem como contexto
//        // Pode disparar uma task assíncrona ou chamar outro service
//
//        return ResponseEntity.ok(agentProcess.toString());
//    }
//}
///*
//*
//*
//        model.addAttribute("travelBrief", travelBrief);
//        new GenericProcessingValues(
//                agentProcess,
//                "Planning your journey",
//                travelBrief.getBrief(),
//                "travelPlan",
//                "journey-plan"
//        ).addToModel(model);
//
//        return "common/processing";
//*
//        Agent agent = agentPlatform.agents().stream()
//                .filter(a -> a.getName().toLowerCase().contains("trip"))
//                .findFirst()
//                .orElseThrow(() -> new IllegalStateException("No travel agent found. Please ensure the tripper agent is registered."));
//
//        AgentProcess agentProcess = agentPlatform.createAgentProcessFrom(
//                agent,
//                ProcessOptions.builder()
//                        .verbosity(new Verbosity(true, true,false, false))
//                        .budget(new Budget(Budget.DEFAULT_TOKEN_LIMIT * 3, 0, 0)).build()
//                ,
//                travelBrief, travelers
//        );
//* */
//
//
//
}
