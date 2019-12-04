package com.dr.process.camunda.command.task;

import com.dr.framework.core.organise.service.OrganisePersonService;
import com.dr.framework.core.process.bo.Comment;
import org.camunda.bpm.engine.impl.interceptor.Command;
import org.camunda.bpm.engine.impl.interceptor.CommandContext;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author dr
 */
public class GetTaskCommentsCmd extends AbstractGetTaskCommentCmd implements Command<List<Comment>> {
    private String taskId;

    public GetTaskCommentsCmd(String taskId, OrganisePersonService organisePersonService) {
        super(organisePersonService);
        this.taskId = taskId;
    }

    @Override
    public List<Comment> execute(CommandContext commandContext) {
        Assert.isTrue(!StringUtils.isEmpty(taskId), "环节id不能为空");
        return commandContext.getProcessEngineConfiguration()
                .getTaskService()
                .getTaskComments(taskId)
                .stream()
                .map(t -> convert(t, commandContext))
                .collect(Collectors.toList());
    }

}
