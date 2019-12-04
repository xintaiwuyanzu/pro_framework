package com.dr.process.camunda.command.task;

import com.dr.framework.core.process.query.TaskQuery;
import com.dr.framework.core.process.service.ProcessService;
import com.dr.process.camunda.query.CustomTaskQuery;
import org.camunda.bpm.engine.impl.interceptor.CommandContext;
import org.springframework.util.StringUtils;

/**
 * @author dr
 */
public class AbstractGetTaskQueryCmd extends AbstractGetTaskCmd {
    TaskQuery query;

    public AbstractGetTaskQueryCmd(TaskQuery query) {
        super(query.isWithVariables(), query.isWithProperty(), query.isWithProcessProperty());
        this.query = query;
    }

    protected org.camunda.bpm.engine.task.TaskQuery convert(CommandContext commandContext) {
        CustomTaskQuery taskQuery = new CustomTaskQuery(commandContext.getProcessEngineConfiguration().getCommandExecutorTxRequired());
        taskQuery.initializeFormKeys();
        if (query != null) {
            if (!StringUtils.isEmpty(query.getCreatePerson())) {
                taskQuery.processVariableValueEquals(ProcessService.CREATE_KEY, query.getCreatePerson());
            }
            if (!StringUtils.isEmpty(query.getOwner())) {
                taskQuery.taskOwner(query.getOwner());
                //TODO
                //taskQuery.taskVariableValueEquals(ProcessService.OWNER_KEY, query.getOwner());
            }
            if (!StringUtils.isEmpty(query.getAssignee())) {
                taskQuery.taskAssignee(query.getAssignee());
            }
            if (!StringUtils.isEmpty(query.getTitle())) {
                taskQuery.taskNameLike(query.getTitle());
            }
            if (!StringUtils.isEmpty(query.getDescription())) {
                taskQuery.taskDescriptionLike(query.getDescription());
            }
            if (!StringUtils.isEmpty(query.getProcessInstanceId())) {
                taskQuery.processInstanceId(query.getProcessInstanceId());
            }
            if (!StringUtils.isEmpty(query.getTaskKeyLike())) {
                taskQuery.taskDefinitionKeyLike(query.getTaskKeyLike());
            }
            if (!StringUtils.isEmpty(query.getTaskKeyNotLike())) {
                taskQuery.taskDefinitionKeyNotLike(query.getTaskKeyNotLike());
            }
            if (!StringUtils.isEmpty(query.getProcessDefinitionKey())) {
                taskQuery.processDefinitionKeyLike(query.getProcessDefinitionKey());
            }
        }
        return taskQuery;
    }

}
