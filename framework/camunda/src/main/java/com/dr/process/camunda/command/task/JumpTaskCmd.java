package com.dr.process.camunda.command.task;

import com.dr.framework.core.process.service.ProcessService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.engine.impl.interceptor.Command;
import org.camunda.bpm.engine.impl.interceptor.CommandContext;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;
import org.camunda.bpm.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.camunda.bpm.engine.impl.persistence.entity.TaskEntity;
import org.camunda.bpm.engine.impl.pvm.process.ActivityImpl;
import org.camunda.bpm.engine.impl.pvm.process.TransitionImpl;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 带备注发送任务给下一环节
 */
public class JumpTaskCmd implements Command<Void> {
    private String taskId;
    private String nextTaskId;
    private String nextPerson;
    private String comment;
    private Map<String, Object> variables;

    public JumpTaskCmd(String taskId, String nextTaskId, String nextPerson, String comment, Map<String, Object> variables) {
        this.taskId = taskId;
        this.nextTaskId = nextTaskId;
        this.nextPerson = nextPerson;
        this.comment = comment;
        this.variables = variables;
    }

    @Override
    public Void execute(CommandContext commandContext) {
        Assert.isTrue(!StringUtils.isEmpty(taskId), "环节Id不能为空！");
        Assert.isTrue(!StringUtils.isEmpty(nextTaskId), "下一环节Id不能为空!");

        TaskService taskService = commandContext.getProcessEngineConfiguration()
                .getTaskService();

        TaskEntity task = commandContext.getTaskManager().findTaskById(taskId);

        ProcessDefinitionEntity processDefinition = task.getProcessDefinition();
        ActivityImpl current = processDefinition.findActivity(task.getTaskDefinitionKey());

        ActivityImpl next = processDefinition.findActivity(nextTaskId);
        Assert.notNull(next, "未查询到下一环节：" + nextTaskId);


        if (!StringUtils.isEmpty(comment)) {
            taskService.createComment(taskId, task.getProcessInstanceId(), comment);
        }
        ExecutionEntity executionEntity = task.getExecution();
        if (variables != null) {
            executionEntity.setVariablesLocal(variables);
        }

        //这里不能用complete，会触发事件，自动跳转到下一个环节
        task.fireEvent(TaskListener.EVENTNAME_COMPLETE);
        commandContext.getTaskManager().deleteTask(task, TaskEntity.DELETE_REASON_COMPLETED, false, true);
        executionEntity.removeTask(task);
        //executionEntity.signal(null, null);

        Map<String, Object> map = new HashMap<>();
        if (!StringUtils.isEmpty(nextPerson)) {
            map.put(ProcessService.ASSIGNEE_KEY, nextPerson);
        }
        executionEntity.setEventSource(current);
        executionEntity.setActivity(current);
        executionEntity.setActive(true);

        TransitionImpl transition = current.createOutgoingTransition();
        transition.setDestination(next);
        executionEntity.leaveActivityViaTransition(transition);
        return null;
    }
}
