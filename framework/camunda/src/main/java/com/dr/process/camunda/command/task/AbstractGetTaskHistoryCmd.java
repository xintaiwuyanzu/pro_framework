package com.dr.process.camunda.command.task;

import com.dr.framework.core.process.bo.TaskObject;
import com.dr.framework.core.process.query.TaskQuery;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.camunda.bpm.engine.history.HistoricTaskInstanceQuery;
import org.camunda.bpm.engine.history.HistoricVariableInstance;
import org.camunda.bpm.engine.impl.interceptor.CommandContext;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.stream.Collectors;

import static com.dr.framework.core.process.service.ProcessService.*;
import static com.dr.process.camunda.command.process.AbstractGetProcessDefinitionCmd.filter;
import static com.dr.process.camunda.command.process.AbstractGetProcessDefinitionCmd.getProperty;

/**
 * @author dr
 */
public class AbstractGetTaskHistoryCmd {
    private TaskQuery query;

    public AbstractGetTaskHistoryCmd(TaskQuery query) {
        this.query = query;
    }

    protected HistoricTaskInstanceQuery convert(CommandContext commandContext) {
        HistoricTaskInstanceQuery hq = commandContext.getProcessEngineConfiguration()
                .getHistoryService()
                .createHistoricTaskInstanceQuery();
        if (query != null) {
            if (!StringUtils.isEmpty(query.getOwner())) {
                hq.taskOwner(query.getOwner());
            }
            if (!StringUtils.isEmpty(query.getAssignee())) {
                hq.taskAssignee(query.getAssignee());
            }
            if (!StringUtils.isEmpty(query.getTaskKeyLike())) {
                hq.taskDefinitionKeyIn(query.getTaskKeyLike());
            }
            if (!StringUtils.isEmpty(query.getTitle())) {
                hq.taskNameLike(query.getTitle());
            }
            if (!StringUtils.isEmpty(query.getDescription())) {
                hq.taskDescriptionLike(query.getDescription());
            }
            if (!StringUtils.isEmpty(query.getProcessInstanceId())) {
                hq.processInstanceId(query.getProcessInstanceId());
            }
        }
        hq.orderByHistoricActivityInstanceStartTime()
                .asc();

        return hq;
    }

    protected TaskObject convert(HistoricTaskInstance his, CommandContext commandContext) {
        if (his == null) {
            return null;
        }
        TaskObject to = new TaskObject();
        Map<String, Object> variables = commandContext.getProcessEngineConfiguration().getHistoryService()
                .createHistoricVariableInstanceQuery()
                .taskIdIn(his.getId())
                .list()
                .stream()
                .collect(Collectors.toMap(HistoricVariableInstance::getName, HistoricVariableInstance::getValue));
        to.setAssignee(his.getAssignee());
        to.setAssigneeName((String) variables.get(ASSIGNEE_NAME_KEY));

        to.setCreateDate(his.getStartTime().getTime());
        //to.setFormKey(task.getFormKey());
        to.setId(his.getId());
        to.setProcessInstanceId(his.getProcessInstanceId());
        to.setProcessDefineId(his.getProcessDefinitionId());
        to.setTaskDefineKey(his.getTaskDefinitionKey());
        to.setOwner(his.getOwner());

        to.setOwnerName((String) variables.get(OWNER_NAME_KEY));

        to.setCreatePerson((String) variables.get(CREATE_KEY));

        to.setCreatePersonName((String) variables.get(CREATE_NAME_KEY));

        to.setName(his.getName());
        to.setDescription(his.getDescription());

        if (query.isWithVariables()) {
            to.setVariables(filter(variables));
        }
        if (query.isWithProperty()) {
            to.setProPerties(getProperty(his.getProcessDefinitionId(),
                    his.getTaskDefinitionKey(),
                    commandContext));
        }
        return to;
    }

}
