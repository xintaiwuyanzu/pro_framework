package com.dr.process.camunda.query;

import org.camunda.bpm.engine.impl.TaskQueryImpl;
import org.camunda.bpm.engine.impl.interceptor.CommandExecutor;

/**
 * 扩展实现一些功能
 *
 * @author dr
 */
public class CustomTaskQuery extends TaskQueryImpl {
    private String taskDefinitionKeyNotLike;
    private String processDefinitionKeyLike;

    public CustomTaskQuery(CommandExecutor commandExecutor) {
        super(commandExecutor);
    }

    public CustomTaskQuery taskDefinitionKeyNotLike(String taskDefinitionKeyNotLike) {
        this.taskDefinitionKeyNotLike = taskDefinitionKeyNotLike;
        return this;
    }

    public CustomTaskQuery processDefinitionKeyLike(String processDefinitionKeyLike) {
        this.processDefinitionKeyLike = processDefinitionKeyLike;
        return this;
    }

    public String getProcessDefinitionKeyLike() {
        return processDefinitionKeyLike;
    }

    public String getTaskDefinitionKeyNotLike() {
        return taskDefinitionKeyNotLike;
    }
}
