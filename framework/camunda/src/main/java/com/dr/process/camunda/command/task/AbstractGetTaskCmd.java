package com.dr.process.camunda.command.task;

import com.dr.framework.core.process.bo.TaskObject;
import org.camunda.bpm.engine.impl.interceptor.CommandContext;
import org.camunda.bpm.engine.impl.persistence.entity.TaskEntity;
import org.camunda.bpm.engine.task.Task;

import java.util.Map;

import static com.dr.framework.core.process.service.ProcessService.*;
import static com.dr.process.camunda.command.process.AbstractGetProcessDefinitionCmd.filter;
import static com.dr.process.camunda.command.process.AbstractGetProcessDefinitionCmd.getProperty;

/**
 * @author dr
 */
public class AbstractGetTaskCmd {
    private boolean withVariables;
    private boolean withProperties;
    private boolean withProcessProperty;

    public AbstractGetTaskCmd(boolean withProperties) {
        this.withProperties = withProperties;
    }

    public AbstractGetTaskCmd(boolean withVariables, boolean withProperties, boolean withProcessProperty) {
        this.withVariables = withVariables;
        this.withProperties = withProperties;
        this.withProcessProperty = withProcessProperty;
    }

    protected TaskObject convert(Task task, CommandContext commandContext) {
        if (task == null) {
            return null;
        }
        TaskObject to = new TaskObject();
        Map<String, Object> variables = commandContext
                .getProcessEngineConfiguration()
                .getTaskService()
                .getVariables(task.getId());
        to.setAssignee(task.getAssignee());
        to.setAssigneeName((String) variables.get(ASSIGNEE_NAME_KEY));

        to.setCreateDate(task.getCreateTime().getTime());

        if (task instanceof TaskEntity) {
            ((TaskEntity) task).initializeFormKey();
        }
        to.setFormKey(task.getFormKey());
        to.setId(task.getId());
        to.setProcessInstanceId(task.getProcessInstanceId());
        to.setProcessDefineId(task.getProcessDefinitionId());
        to.setTaskDefineKey(task.getTaskDefinitionKey());
        to.setOwner(task.getOwner());

        to.setOwnerName((String) variables.get(OWNER_NAME_KEY));

        to.setCreatePerson((String) variables.get(CREATE_KEY));

        to.setCreatePersonName((String) variables.get(CREATE_NAME_KEY));

        to.setName(task.getName());
        to.setDescription(task.getDescription());

        to.setSuspend(task.isSuspended());
        if (withVariables) {
            to.setVariables(filter(variables));
        }
        if (withProperties) {
            to.setProPerties(getProperty(task.getProcessDefinitionId(),
                    task.getTaskDefinitionKey(),
                    commandContext));
        }
        if (withProcessProperty) {
            to.setProcessProPerties(getProperty(task.getProcessDefinitionId(),
                    task.getProcessDefinitionId().split(":")[0],
                    commandContext));
        }
        return to;
    }

}
