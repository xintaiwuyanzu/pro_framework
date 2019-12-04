package com.dr.process.camunda.command.process;

import com.dr.framework.common.page.Page;
import com.dr.framework.core.process.bo.ProcessObject;
import com.dr.framework.core.process.query.ProcessQuery;
import org.camunda.bpm.engine.history.HistoricProcessInstanceQuery;
import org.camunda.bpm.engine.impl.interceptor.Command;
import org.camunda.bpm.engine.impl.interceptor.CommandContext;

import java.util.stream.Collectors;

/**
 * @author dr
 */
public class GetProcessObjectHistoryPageCmd extends AbstractGetProcessObjectHistoryCmd implements Command<Page<ProcessObject>> {
    private int start;
    private int end;

    public GetProcessObjectHistoryPageCmd(ProcessQuery query, int start, int end) {
        super(query);
        this.start = start;
        this.end = end;
    }

    @Override
    public Page<ProcessObject> execute(CommandContext commandContext) {
        HistoricProcessInstanceQuery query = convert(commandContext);
        return new Page<>(
                start,
                end - start,
                query.count(),
                () -> query.list()
                        .stream()
                        .map(p -> convert(p, commandContext))
                        .collect(Collectors.toList())
        );
    }
}
