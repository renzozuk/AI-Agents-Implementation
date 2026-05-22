package com.seap.agent;

import com.embabel.agent.api.tool.Tool;
import com.seap.tools.ApiTools;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AgentConfig {

    private final ApiTools apiTools;
    private final SeapAgent seapAgent;

    public AgentConfig(ApiTools apiTools, SeapAgent seapAgent) {
        this.apiTools = apiTools;
        this.seapAgent = seapAgent;
    }

    public void configurarAgent() {
        seapAgent.tools.add((Tool) this.apiTools);
    }
}
