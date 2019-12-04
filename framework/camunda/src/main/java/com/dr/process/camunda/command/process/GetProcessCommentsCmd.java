package com.dr.process.camunda.command.process;

import com.dr.framework.core.organise.service.OrganisePersonService;
import com.dr.framework.core.process.bo.Comment;
import com.dr.process.camunda.command.task.AbstractGetTaskCommentCmd;
import org.camunda.bpm.engine.impl.interceptor.Command;
import org.camunda.bpm.engine.impl.interceptor.CommandContext;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author dr
 */
public class GetProcessCommentsCmd extends AbstractGetTaskCommentCmd implements Command<List<Comment>> {
    private String processInstanceId;

    public GetProcessCommentsCmd(String processInstanceId, OrganisePersonService organisePersonService) {
        super(organisePersonService);
        this.processInstanceId = processInstanceId;
    }

    @Override
    public List<Comment> execute(CommandContext commandContext) {
        Assert.isTrue(!StringUtils.isEmpty(processInstanceId), "流程实例id不能为空");
        return commandContext.getProcessEngineConfiguration()
                .getTaskService()
                .getProcessInstanceComments(processInstanceId)
                .stream()
                .map(t -> convert(t, commandContext))
                .collect(Collectors.toList());
    }

}
