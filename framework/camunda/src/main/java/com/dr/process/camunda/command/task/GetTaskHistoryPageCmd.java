package com.dr.process.camunda.command.task;

import com.dr.framework.common.page.Page;
import com.dr.framework.core.process.bo.TaskObject;
import com.dr.framework.core.process.query.TaskQuery;
import org.camunda.bpm.engine.history.HistoricTaskInstanceQuery;
import org.camunda.bpm.engine.impl.interceptor.Command;
import org.camunda.bpm.engine.impl.interceptor.CommandContext;

import java.util.stream.Collectors;

/**
 * @author dr
 */
public class GetTaskHistoryPageCmd extends AbstractGetTaskHistoryCmd implements Command<Page<TaskObject>> {
    private int start;
    private int end;

    public GetTaskHistoryPageCmd(TaskQuery query, int start, int end) {
        super(query);
        this.start = start;
        this.end = end;
    }

    @Override
    public Page<TaskObject> execute(CommandContext commandContext) {
        HistoricTaskInstanceQuery hq = convert(commandContext);
        return new Page<TaskObject>(
                start,
                end - start,
                hq.count(),
                () -> hq.listPage(start, end)
                        .stream()
                        .map(h -> convert(h, commandContext))
                        .collect(Collectors.toList())
        );
    }
}
