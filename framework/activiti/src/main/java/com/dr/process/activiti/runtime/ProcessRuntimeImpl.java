package com.dr.process.activiti.runtime;

import org.activiti.api.model.shared.model.VariableInstance;
import org.activiti.api.process.model.ProcessDefinition;
import org.activiti.api.process.model.ProcessDefinitionMeta;
import org.activiti.api.process.model.ProcessInstance;
import org.activiti.api.process.model.ProcessInstanceMeta;
import org.activiti.api.process.model.payloads.*;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.process.runtime.conf.ProcessRuntimeConfiguration;
import org.activiti.api.runtime.shared.query.Page;
import org.activiti.api.runtime.shared.query.Pageable;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.runtime.api.model.impl.APIProcessDefinitionConverter;
import org.activiti.runtime.api.model.impl.APIProcessInstanceConverter;
import org.activiti.runtime.api.model.impl.APIVariableInstanceConverter;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

/**
 * activiti 流程管理器，主要是嵌入自己的权限相关的模块
 *
 * @author dr
 */
public class ProcessRuntimeImpl implements ProcessRuntime {
    private final RepositoryService repositoryService;

    private final APIProcessDefinitionConverter processDefinitionConverter;

    private final RuntimeService runtimeService;

    private final APIProcessInstanceConverter processInstanceConverter;

    private final APIVariableInstanceConverter variableInstanceConverter;

    private final ProcessRuntimeConfiguration configuration;

    private final ApplicationEventPublisher eventPublisher;

    public ProcessRuntimeImpl(RepositoryService repositoryService,
                              APIProcessDefinitionConverter processDefinitionConverter,
                              RuntimeService runtimeService,
                              APIProcessInstanceConverter processInstanceConverter,
                              APIVariableInstanceConverter variableInstanceConverter,
                              ProcessRuntimeConfiguration configuration,
                              ApplicationEventPublisher eventPublisher) {
        this.repositoryService = repositoryService;
        this.processDefinitionConverter = processDefinitionConverter;
        this.runtimeService = runtimeService;
        this.processInstanceConverter = processInstanceConverter;
        this.variableInstanceConverter = variableInstanceConverter;
        this.configuration = configuration;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public ProcessRuntimeConfiguration configuration() {
        return configuration;
    }

    @Override
    public ProcessDefinition processDefinition(String processDefinitionId) {
        return null;
    }

    @Override
    public Page<ProcessDefinition> processDefinitions(Pageable pageable) {
        return null;
    }

    @Override
    public Page<ProcessDefinition> processDefinitions(Pageable pageable, GetProcessDefinitionsPayload getProcessDefinitionsPayload) {
        return null;
    }

    @Override
    public ProcessInstance start(StartProcessPayload startProcessPayload) {
        return null;
    }

    @Override
    public Page<ProcessInstance> processInstances(Pageable pageable) {
        return null;
    }

    @Override
    public Page<ProcessInstance> processInstances(Pageable pageable, GetProcessInstancesPayload getProcessInstancesPayload) {
        return null;
    }

    @Override
    public ProcessInstance processInstance(String processInstanceId) {
        return null;
    }

    @Override
    public ProcessInstance suspend(SuspendProcessPayload suspendProcessPayload) {
        return null;
    }

    @Override
    public ProcessInstance resume(ResumeProcessPayload resumeProcessPayload) {
        return null;
    }

    @Override
    public ProcessInstance delete(DeleteProcessPayload deleteProcessPayload) {
        return null;
    }

    @Override
    public ProcessInstance update(UpdateProcessPayload updateProcessPayload) {
        return null;
    }

    @Override
    public void signal(SignalPayload signalPayload) {

    }

    @Override
    public ProcessDefinitionMeta processDefinitionMeta(String processDefinitionKey) {
        return null;
    }

    @Override
    public ProcessInstanceMeta processInstanceMeta(String processInstanceId) {
        return null;
    }

    @Override
    public List<VariableInstance> variables(GetVariablesPayload getVariablesPayload) {
        return null;
    }

    @Override
    public void removeVariables(RemoveProcessVariablesPayload removeProcessVariablesPayload) {

    }

    @Override
    public void setVariables(SetProcessVariablesPayload setProcessVariablesPayload) {

    }

    @Override
    public void receive(ReceiveMessagePayload messagePayload) {

    }

    @Override
    public ProcessInstance start(StartMessagePayload messagePayload) {
        return null;
    }
}
