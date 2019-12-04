package com.dr.process.camunda.command.task;

import com.dr.framework.core.process.bo.TaskDefinition;
import org.camunda.bpm.engine.impl.interceptor.Command;
import org.camunda.bpm.engine.impl.interceptor.CommandContext;
import org.camunda.bpm.engine.impl.persistence.entity.TaskEntity;
import org.camunda.bpm.engine.impl.pvm.process.ActivityImpl;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author dr
 */
public class GetNextTaskDefinitionCmd extends AbstractGetTaskDefinitionCmd implements Command<List<TaskDefinition>> {

    private String taskId;

    public GetNextTaskDefinitionCmd(String taskId) {
        super(true, true, false);
        this.taskId = taskId;
    }

    @Override
    public List<TaskDefinition> execute(CommandContext commandContext) {
        TaskEntity task = commandContext.getTaskManager().findTaskById(taskId);
        ActivityImpl activity = task.getProcessDefinition().findActivity(task.getTaskDefinitionKey());
        if (activity != null) {
            return activity.getOutgoingTransitions()
                    .stream()
                    .map(o -> convert((ActivityImpl) o.getDestination(), commandContext))
                    .collect(Collectors.toList());
        }
        return null;
    }

}
