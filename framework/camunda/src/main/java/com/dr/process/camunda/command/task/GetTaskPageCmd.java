package com.dr.process.camunda.command.task;

import com.dr.framework.common.page.Page;
import com.dr.framework.core.process.bo.TaskObject;
import com.dr.framework.core.process.query.TaskQuery;
import org.camunda.bpm.engine.impl.interceptor.Command;
import org.camunda.bpm.engine.impl.interceptor.CommandContext;

import java.util.stream.Collectors;

/**
 * @author dr
 */
public class GetTaskPageCmd extends AbstractGetTaskQueryCmd implements Command<Page<TaskObject>> {
    private int start;
    private int end;

    public GetTaskPageCmd(TaskQuery query, int start, int end) {
        super(query);
        this.start = start;
        this.end = end;
    }

    @Override
    public Page<TaskObject> execute(CommandContext commandContext) {
        org.camunda.bpm.engine.task.TaskQuery query = convert(commandContext);
        return new Page<>(
                start,
                end - start,
                query.count(),
                () -> query.list()
                        .stream()
                        .map(t -> convert(t, commandContext))
                        .collect(Collectors.toList())
        );
    }
}
