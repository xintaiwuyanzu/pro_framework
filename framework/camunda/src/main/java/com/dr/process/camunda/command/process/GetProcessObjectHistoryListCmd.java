package com.dr.process.camunda.command.process;

import com.dr.framework.core.process.bo.ProcessObject;
import com.dr.framework.core.process.query.ProcessQuery;
import org.camunda.bpm.engine.impl.interceptor.Command;
import org.camunda.bpm.engine.impl.interceptor.CommandContext;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author dr
 */
public class GetProcessObjectHistoryListCmd extends AbstractGetProcessObjectHistoryCmd implements Command<List<ProcessObject>> {
    public GetProcessObjectHistoryListCmd(ProcessQuery query) {
        super(query);
    }

    @Override
    public List<ProcessObject> execute(CommandContext commandContext) {
        return convert(commandContext)
                .list()
                .stream()
                .map(p -> convert(p, commandContext))
                .collect(Collectors.toList());
    }

}
