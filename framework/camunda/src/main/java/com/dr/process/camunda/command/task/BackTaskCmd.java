package com.dr.process.camunda.command.task;

import org.camunda.bpm.engine.impl.interceptor.Command;
import org.camunda.bpm.engine.impl.interceptor.CommandContext;
import org.camunda.bpm.engine.impl.persistence.entity.TaskEntity;
import org.camunda.bpm.engine.impl.pvm.PvmTransition;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 退回到上一环节
 *
 * @author dr
 */
public class BackTaskCmd implements Command<Void> {
    private String taskId;
    private String comment;

    public BackTaskCmd(String taskId, String comment) {
        this.taskId = taskId;
        this.comment = comment;
    }

    @Override
    public Void execute(CommandContext commandContext) {
        Assert.isTrue(!StringUtils.isEmpty(taskId), "环节id不能为空!");
        TaskEntity taskEntity = commandContext.getTaskManager().findTaskById(taskId);

        List<PvmTransition> transitions =
                taskEntity.getProcessDefinition().findActivity(taskEntity.getTaskDefinitionKey()).getIncomingTransitions();
        Assert.isTrue(!transitions.isEmpty(), "未找到当前环节的上一环节！");
        Assert.isTrue(transitions.size() == 1, "多环节功能暂未处理！");
        commandContext.getProcessEngineConfiguration()
                .getCommandExecutorTxRequired()
                .execute(new JumpTaskCmd(taskId,
                        transitions.get(0).getSource().getId(),
                        taskEntity.getOwner(),
                        comment,
                        null
                ));
        return null;
    }
}
