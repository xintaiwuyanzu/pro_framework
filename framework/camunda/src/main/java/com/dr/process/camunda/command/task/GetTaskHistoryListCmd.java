package com.dr.process.camunda.command.task;

import com.dr.framework.core.process.bo.TaskObject;
import com.dr.framework.core.process.query.TaskQuery;
import org.camunda.bpm.engine.impl.interceptor.Command;
import org.camunda.bpm.engine.impl.interceptor.CommandContext;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author dr
 */
public class GetTaskHistoryListCmd extends AbstractGetTaskHistoryCmd implements Command<List<TaskObject>> {

    public GetTaskHistoryListCmd(TaskQuery query) {
        super(query);
    }

    @Override
    public List<TaskObject> execute(CommandContext commandContext) {
        return convert(commandContext)
                .list()
                .stream()
                .map(o -> convert(o, commandContext))
                .collect(Collectors.toList());
    }
}
