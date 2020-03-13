package com.dr.process.camunda.command.process;

import com.dr.framework.core.process.bo.ProcessDefinition;
import org.camunda.bpm.engine.impl.interceptor.Command;
import org.camunda.bpm.engine.impl.interceptor.CommandContext;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * 根据流程定义id查询流程详细信息
 *
 * @author dr
 */
public class GetProcessDefinitionByIdCommand extends AbstractGetProcessDefinitionCmd implements Command<ProcessDefinition> {
    private String processDefinitionId;

    public GetProcessDefinitionByIdCommand(String processDefinitionId) {
        super(true, true);
        this.processDefinitionId = processDefinitionId;
    }

    public GetProcessDefinitionByIdCommand(String processDefinitionId, boolean withProperty) {
        super(withProperty);
        this.processDefinitionId = processDefinitionId;
    }

    @Override
    public ProcessDefinition execute(CommandContext commandContext) {
        Assert.isTrue(!StringUtils.isEmpty(processDefinitionId), "流程定义id不能为空！");
        org.camunda.bpm.engine.repository.ProcessDefinition processDefinition = commandContext.getProcessEngineConfiguration().getRepositoryService()
                .getProcessDefinition(processDefinitionId);
        return convert(processDefinition, commandContext);
    }
}
