package com.dr.process.camunda.command.task;

import com.dr.framework.core.process.bo.TaskDefinition;
import com.dr.process.camunda.command.process.AbstractGetProcessDefinitionCmd;
import org.camunda.bpm.engine.impl.core.model.PropertyKey;
import org.camunda.bpm.engine.impl.interceptor.CommandContext;
import org.camunda.bpm.engine.impl.pvm.process.ActivityImpl;

/**
 * @author dr
 */
public class AbstractGetTaskDefinitionCmd {
    private boolean withProperty;
    private boolean withProcessProperty;
    private boolean withStartUser;


    static final PropertyKey<String> documentation = new PropertyKey<>("documentation");

    public AbstractGetTaskDefinitionCmd(boolean withProperty, boolean withProcessProperty, boolean withStartUser) {
        this.withProperty = withProperty;
        this.withProcessProperty = withProcessProperty;
        this.withStartUser = withStartUser;
    }

    protected TaskDefinition convert(ActivityImpl activity, CommandContext commandContext) {
        if (activity == null) {
            return null;
        }
        TaskDefinition taskDefinition = new TaskDefinition();
        taskDefinition.setId(activity.getId());
        taskDefinition.setName(activity.getName());
        taskDefinition.setDescription(activity.getProperties().get(documentation));
        if (withProperty) {
            taskDefinition.setProPerties(AbstractGetProcessDefinitionCmd.getProperty(
                    activity.getProcessDefinition().getId(),
                    activity.getId(),
                    commandContext));
        }
        if (withProcessProperty) {
            taskDefinition.setProcessProPerties(AbstractGetProcessDefinitionCmd.getProperty(
                    activity.getProcessDefinition().getId(),
                    activity.getProcessDefinition().getId().split(":")[0],
                    commandContext));
        }
        return taskDefinition;
    }

}
