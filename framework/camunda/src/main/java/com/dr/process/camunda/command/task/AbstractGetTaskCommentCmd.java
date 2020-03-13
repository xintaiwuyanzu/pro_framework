package com.dr.process.camunda.command.task;

import com.dr.framework.core.organise.entity.Person;
import com.dr.framework.core.organise.service.OrganisePersonService;
import com.dr.framework.core.process.bo.Comment;
import org.camunda.bpm.engine.impl.interceptor.CommandContext;
import org.springframework.util.StringUtils;

/**
 * @author dr
 */
public class AbstractGetTaskCommentCmd {
    private OrganisePersonService organisePersonService;

    public AbstractGetTaskCommentCmd(OrganisePersonService organisePersonService) {
        this.organisePersonService = organisePersonService;
    }

    protected Comment convert(org.camunda.bpm.engine.task.Comment t, CommandContext commandContext) {
        if (t == null) {
            return null;
        }
        Comment comment = new Comment();
        comment.setCreateDate(t.getTime().getTime());
        String personId = t.getUserId();
        if (!StringUtils.isEmpty(personId)) {
            Person person = organisePersonService.getPersonById(personId);
            if (person != null) {
                comment.setCreateUser(personId);
                comment.setCreateUserName(person.getUserName());
            }
        }
        comment.setId(t.getId());
        comment.setMessage(t.getFullMessage());
        comment.setProcessInstanceId(t.getProcessInstanceId());
        comment.setTaskId(t.getTaskId());
        return comment;
    }
}
