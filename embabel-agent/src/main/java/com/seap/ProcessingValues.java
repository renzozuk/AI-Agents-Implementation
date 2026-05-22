package com.seap;

import com.embabel.agent.core.AgentProcess;
import org.springframework.ui.Model;

import java.util.Objects;

public class ProcessingValues {

    private final AgentProcess agentProcess;
    private final String title;
    private final String value;

    public ProcessingValues(AgentProcess agentProcess, String title, String value) {
        this.agentProcess = agentProcess;
        this.title = title;
        this.value = value;
    }

    public void addToModel(Model model) {
        model.addAttribute("processId", agentProcess.getId());
        model.addAttribute("title", title);
        model.addAttribute("value", value);
    }

    public AgentProcess getAgentProcess() { return agentProcess; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProcessingValues that = (ProcessingValues) o;
        if (!Objects.equals(agentProcess, that.agentProcess)) return false;
        if (!Objects.equals(title, that.title)) return false;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(agentProcess, title, value);
    }
}
