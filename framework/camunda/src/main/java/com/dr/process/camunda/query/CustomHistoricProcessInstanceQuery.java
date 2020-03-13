package com.dr.process.camunda.query;

import org.camunda.bpm.engine.impl.HistoricProcessInstanceQueryImpl;
import org.camunda.bpm.engine.impl.interceptor.CommandExecutor;

/**
 * @author dr
 */
public class CustomHistoricProcessInstanceQuery extends HistoricProcessInstanceQueryImpl {
    private String processDefKeyLike;

    public CustomHistoricProcessInstanceQuery(CommandExecutor commandExecutor) {
        super(commandExecutor);
    }

    public String getProcessDefKeyLike() {
        return processDefKeyLike;
    }

    public void setProcessDefKeyLike(String processDefKeyLike) {
        this.processDefKeyLike = processDefKeyLike;
    }
}
