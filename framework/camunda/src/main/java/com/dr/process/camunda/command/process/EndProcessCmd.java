package com.dr.process.camunda.command.process;

import com.dr.process.camunda.command.task.JumpTaskCmd;
import org.camunda.bpm.engine.impl.bpmn.behavior.NoneEndEventActivityBehavior;
import org.camunda.bpm.engine.impl.interceptor.Command;
import org.camunda.bpm.engine.impl.interceptor.CommandContext;
import org.camunda.bpm.engine.impl.persistence.entity.TaskEntity;
import org.camunda.bpm.engine.impl.pvm.PvmTransition;
import org.camunda.bpm.engine.impl.pvm.process.ActivityImpl;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.EndEvent;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.List;

/**
 * 办结流程，不管流程处在哪个环节
 *
 * @author dr
 */
public class EndProcessCmd implements Command<Void> {
    private String taskId;
    private String comment;

    public EndProcessCmd(String taskId, String comment) {
        this.taskId = taskId;
        this.comment = comment;
    }

    @Override
    public Void execute(CommandContext commandContext) {
        Assert.isTrue(!StringUtils.isEmpty(taskId), "任务id不能为空");
        TaskEntity taskEntity = commandContext.getTaskManager().findTaskById(taskId);
        if (!StringUtils.isEmpty(comment)) {
            commandContext.getProcessEngineConfiguration()
                    .getTaskService()
                    .createComment(taskId, taskEntity.getProcessInstanceId(), comment);
        }
        ActivityImpl activity = taskEntity.getProcessDefinition().findActivity(taskEntity.getTaskDefinitionKey());
        List<PvmTransition> transitions = activity.getOutgoingTransitions();
        if (transitions.size() == 1 &&
                transitions.get(0)
                        .getDestination()
                        .getActivityBehavior() instanceof NoneEndEventActivityBehavior) {
            taskEntity.complete();
        } else {
            BpmnModelInstance bpmnModelInstance = commandContext.getProcessEngineConfiguration()
                    .getRepositoryService()
                    .getBpmnModelInstance(taskEntity.getProcessDefinitionId());
            Collection<EndEvent> endEvents = bpmnModelInstance.getModelElementById(taskEntity.getProcessDefinition().getKey())
                    .getChildElementsByType(EndEvent.class);
            //先查到end的id，然后执行jumpcmd
            commandContext.getProcessEngineConfiguration()
                    .getCommandExecutorTxRequired()
                    .execute(new JumpTaskCmd(taskId, endEvents.iterator().next().getId(), null, null, null));
        }
        return null;
    }
}
