package com.dr.process.camunda.command.process;

import org.camunda.bpm.engine.impl.interceptor.CommandContext;
import org.camunda.bpm.engine.repository.ProcessDefinitionQuery;
import org.springframework.util.StringUtils;

/**
 * @author dr
 */
public abstract class AbstractGetProcessQueryCmd extends AbstractGetProcessDefinitionCmd {
    public AbstractGetProcessQueryCmd(boolean withProperty) {
        super(withProperty);
    }

    protected ProcessDefinitionQuery convert(com.dr.framework.core.process.query.ProcessDefinitionQuery query, CommandContext commandContext) {
        ProcessDefinitionQuery pq = commandContext.getProcessEngineConfiguration()
                .getRepositoryService()
                .createProcessDefinitionQuery()
                .latestVersion();
        if (query != null) {
            if (!StringUtils.isEmpty(query.getName())) {
                pq.processDefinitionNameLike(query.getName());
            }
            if (!StringUtils.isEmpty(query.getType())) {
                pq.processDefinitionKeyLike(query.getType());
            }
        }
        pq.orderByProcessDefinitionKey().asc();
        return pq;
    }

}
