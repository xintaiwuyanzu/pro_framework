package com.dr.process.camunda.command.task;

import com.dr.framework.core.process.bo.TaskDefinition;
import org.camunda.bpm.engine.impl.bpmn.helper.BpmnProperties;
import org.camunda.bpm.engine.impl.context.Context;
import org.camunda.bpm.engine.impl.interceptor.Command;
import org.camunda.bpm.engine.impl.interceptor.CommandContext;
import org.camunda.bpm.engine.runtime.ActivityInstance;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author dr
 */
public class GetProcessTaskDefinitionCmd extends AbstractGetTaskDefinitionCmd implements Command<List<TaskDefinition>> {
    private String processInstanceId;

    public GetProcessTaskDefinitionCmd(String processInstanceId) {
        super(true, true, false);
        this.processInstanceId = processInstanceId;
    }

    @Override
    public List<TaskDefinition> execute(CommandContext commandContext) {
        Assert.isTrue(!StringUtils.isEmpty(processInstanceId), "流程实例id不能为空!");
        ActivityInstance activityInstance = commandContext.getProcessEngineConfiguration().getRuntimeService().getActivityInstance(processInstanceId);
        return Context
                .getProcessEngineConfiguration()
                .getDeploymentCache()
                .findDeployedProcessDefinitionById(activityInstance.getProcessDefinitionId())
                .getActivities()
                .stream()
                .filter(o -> o.getProperties().get(BpmnProperties.TYPE).endsWith("Task"))
                .map(o -> convert(o, commandContext))
                .collect(Collectors.toList());
    }
}
