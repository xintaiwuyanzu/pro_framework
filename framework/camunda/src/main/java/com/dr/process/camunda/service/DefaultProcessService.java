package com.dr.process.camunda.service;

import com.dr.framework.common.page.Page;
import com.dr.framework.core.organise.entity.Person;
import com.dr.framework.core.organise.service.OrganisePersonService;
import com.dr.framework.core.process.bo.*;
import com.dr.framework.core.process.query.ProcessDefinitionQuery;
import com.dr.framework.core.process.query.ProcessQuery;
import com.dr.framework.core.process.query.TaskQuery;
import com.dr.framework.core.process.service.ProcessService;
import com.dr.process.camunda.command.process.*;
import com.dr.process.camunda.command.task.*;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.impl.interceptor.CommandExecutor;
import org.camunda.bpm.engine.runtime.ProcessInstanceWithVariables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * camunda 流程引擎默认实现
 *
 * @author dr
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class DefaultProcessService implements ProcessService {
    @Lazy
    @Autowired
    CommandExecutor commandExecutor;
    @Autowired
    TaskService taskService;
    @Autowired
    RuntimeService runtimeService;
    @Autowired
    OrganisePersonService organisePersonService;


    @Override
    public ProcessObject start(String processId, Map<String, Object> variMap, Person person) {
        ProcessDefinition processDefinition = getProcessDefinitionById(processId);
        ProPerty proPerty = processDefinition.getProPerty(FORM_URL_KEY);
        if (proPerty != null) {
            variMap.put(FORM_URL_KEY, proPerty.getValue());
        }
        ProcessInstanceWithVariables instance = (ProcessInstanceWithVariables) runtimeService.startProcessInstanceById(processDefinition.getId(), variMap);
        return commandExecutor.execute(
                new ConvertProcessInstanceCmd(
                        instance.getId(),
                        instance.getVariables()
                )
        );
    }

    @Override
    public List<ProcessObject> processObjectList(ProcessQuery query) {
        return commandExecutor.execute(new GetProcessObjectListCmd(query));
    }

    @Override
    public Page<ProcessObject> processObjectPage(ProcessQuery query, int start, int end) {
        return commandExecutor.execute(new GetProcessObjectPageCmd(query, start, end));
    }

    @Override
    public List<ProcessObject> processObjectHistoryList(ProcessQuery query) {
        return commandExecutor.execute(new GetProcessObjectHistoryListCmd(query));
    }

    @Override
    public Page<ProcessObject> processObjectHistoryPage(ProcessQuery query, int start, int end) {
        return commandExecutor.execute(new GetProcessObjectHistoryPageCmd(query, start, end));
    }


    @Override
    public ProcessDefinition getProcessDefinitionById(String id) {
        return commandExecutor.execute(new GetProcessDefinitionByIdCommand(id));
    }

    @Override
    public ProcessDefinition getProcessDefinitionByKey(String key) {
        return commandExecutor.execute(new GetProcessDefinitionByKeyCommand(key));
    }

    @Override
    public List<ProcessDefinition> processDefinitionList(ProcessDefinitionQuery processDefinitionQuery) {
        return commandExecutor.execute(new GetProcessDefinitionListCmd(processDefinitionQuery));
    }

    @Override
    public Page<ProcessDefinition> processDefinitionPage(ProcessDefinitionQuery processDefinitionQuery, int start, int end) {
        return commandExecutor.execute(new GetProcessDefinitionPageCmd(processDefinitionQuery, start, end));
    }

    @Override
    public void complete(String taskId, Map<String, Object> variables) {
        Assert.isTrue(!StringUtils.isEmpty(taskId), "环节Id不能为空！");
        taskService.complete(taskId, variables);
    }

    @Override
    public void setProcessVariable(String taskId, Map<String, Object> variables) {
        taskService.setVariables(taskId, variables);
    }

    @Override
    public void setTaskVariable(String taskId, Map<String, Object> variables) {
        taskService.setVariablesLocal(taskId, variables);
    }

    @Override
    public Map<String, Object> getTaskVariables(String taskId) {
        return taskService.getVariablesLocal(taskId);
    }

    @Override
    public Map<String, Object> getProcessVariables(String taskId) {
        return taskService.getVariables(taskId);
    }

    @Override
    public void removeTaskVariable(String taskId, String... variNames) {
        List<String> strings = Arrays.asList(variNames);
        taskService.removeVariablesLocal(taskId, strings);
    }

    @Override
    public void removeProcessVariable(String taskId, String... variNames) {
        List<String> strings = Arrays.asList(variNames);
        taskService.removeVariables(taskId, strings);
    }

    @Override
    public List<Comment> taskComments(String taskId) {
        return commandExecutor.execute(new GetTaskCommentsCmd(taskId, organisePersonService));
    }

    @Override
    public List<Comment> processComments(String processInstanceId) {
        return commandExecutor.execute(new GetProcessCommentsCmd(processInstanceId, organisePersonService));
    }

    @Override
    public void addComment(String taskId, String... comments) {
        TaskObject taskObject = taskInfo(taskId);
        for (String comment : comments) {
            taskService.createComment(taskObject.getId(), taskObject.getProcessInstanceId(), comment);
        }
    }

    @Override
    public TaskObject taskInfo(String taskId) {
        return commandExecutor.execute(new GetTaskCmd(taskId));
    }

    @Override
    public List<TaskObject> taskList(TaskQuery query) {
        return commandExecutor.execute(new GetTaskListCmd(query));
    }

    @Override
    public Page<TaskObject> taskPage(TaskQuery query, int start, int end) {
        return commandExecutor.execute(new GetTaskPageCmd(query, start, end));
    }

    @Override
    public List<TaskObject> taskHistoryList(TaskQuery query) {
        return commandExecutor.execute(new GetTaskHistoryListCmd(query));
    }

    @Override
    public Page<TaskObject> taskHistoryPage(TaskQuery query, int start, int end) {
        return commandExecutor.execute(new GetTaskHistoryPageCmd(query, start, end));
    }

    @Override
    public List<TaskDefinition> nextTasks(String taskId) {
        return commandExecutor.execute(new GetNextTaskDefinitionCmd(taskId));
    }

    @Override
    public List<TaskDefinition> processTaskDefinitions(String processInstanceId) {
        return commandExecutor.execute(new GetProcessTaskDefinitionCmd(processInstanceId));
    }

    @Override
    public void send(String taskId, String nextPerson, String comment, Map<String, Object> variables) {
        commandExecutor.execute(new SendTaskCmd(taskId, nextPerson, comment, variables));
    }

    @Override
    public void suspend(String processInstanceId) {
        Assert.isTrue(!StringUtils.isEmpty(processInstanceId), "流程实例不能为空");
        runtimeService.suspendProcessInstanceById(processInstanceId);
    }

    @Override
    public void active(String processInstanceId) {
        Assert.isTrue(!StringUtils.isEmpty(processInstanceId), "流程实例不能为空");
        runtimeService.activateProcessInstanceById(processInstanceId);
    }

    @Override
    public void endProcess(String taskId, String comment) {
        commandExecutor.execute(new EndProcessCmd(taskId, comment));
    }

    @Override
    public void back(String taskId, String comment) {
        commandExecutor.execute(new BackTaskCmd(taskId, comment));
    }

    @Override
    public void jump(String taskId, String nextTaskId, String nextPerson, String comment, Map<String, Object> variables) {
        commandExecutor.execute(new JumpTaskCmd(taskId, nextTaskId, nextPerson, comment, variables));
    }

    @Override
    public void delete(String processInstance, String deleteReason) {
        Assert.isTrue(!StringUtils.isEmpty(processInstance), "流程实例不能为空！");
        runtimeService.deleteProcessInstance(processInstance, deleteReason);
    }
}
