package com.dr.process.camunda.command.process;

import com.dr.framework.core.process.bo.ProcessDefinition;
import com.dr.framework.core.process.query.ProcessDefinitionQuery;
import com.dr.process.camunda.command.process.AbstractGetProcessQueryCmd;
import org.camunda.bpm.engine.impl.interceptor.Command;
import org.camunda.bpm.engine.impl.interceptor.CommandContext;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author dr
 */
public class GetProcessDefinitionListCmd extends AbstractGetProcessQueryCmd implements Command<List<ProcessDefinition>> {
    private ProcessDefinitionQuery query;

    public GetProcessDefinitionListCmd(ProcessDefinitionQuery processDefinitionQuery) {
        super(processDefinitionQuery.isWithProperty());
        this.query = processDefinitionQuery;
    }

    @Override
    public List<ProcessDefinition> execute(CommandContext commandContext) {
        return convert(query, commandContext)
                .list()
                .stream()
                .map(p -> convert(p, commandContext))
                .collect(Collectors.toList());
    }
}
