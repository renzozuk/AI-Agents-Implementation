
package com.seap;

import com.embabel.agent.core.AgentProcess;
import org.springframework.ui.Model;

import java.util.Objects;

public class GenericProcessingValues {

    private final AgentProcess agentProcess;
    private final String pageTitle;
    private final String detail;
    private final String resultModelKey;
    private final String successView;

    public GenericProcessingValues(AgentProcess agentProcess, String pageTitle, String detail, String resultModelKey, String successView) {
        this.agentProcess = agentProcess;
        this.pageTitle = pageTitle;
        this.detail = detail;
        this.resultModelKey = resultModelKey;
        this.successView = successView;
    }

    public void addToModel(Model model) {
        model.addAttribute("processId", agentProcess.getId());
        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("detail", detail);
        model.addAttribute("resultModelKey", resultModelKey);
        model.addAttribute("successView", successView);
    }

    public AgentProcess getAgentProcess() {
        return agentProcess;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public String getDetail() {
        return detail;
    }

    public String getResultModelKey() {
        return resultModelKey;
    }

    public String getSuccessView() {
        return successView;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GenericProcessingValues that = (GenericProcessingValues) o;
        return Objects.equals(agentProcess, that.agentProcess) &&
                Objects.equals(pageTitle, that.pageTitle) &&
                Objects.equals(detail, that.detail) &&
                Objects.equals(resultModelKey, that.resultModelKey) &&
                Objects.equals(successView, that.successView);
    }

    @Override
    public int hashCode() {
        return Objects.hash(agentProcess, pageTitle, detail, resultModelKey, successView);
    }

    @Override
    public String toString() {
        return "GenericProcessingValues{"
                + "agentProcess=" + agentProcess +
                ", pageTitle='" + pageTitle + '\'' +
                ", detail='" + detail + '\'' +
                ", resultModelKey='" + resultModelKey + '\'' +
                ", successView='" + successView + '\'' +
                '}';
    }
}
