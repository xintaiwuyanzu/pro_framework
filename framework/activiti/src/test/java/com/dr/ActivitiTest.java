package com.dr;

import org.activiti.api.process.model.ProcessInstance;
import org.activiti.api.process.model.ProcessInstanceMeta;
import org.activiti.api.process.model.builders.ProcessPayloadBuilder;
import org.activiti.api.process.model.payloads.StartProcessPayload;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.runtime.shared.query.Page;
import org.activiti.api.runtime.shared.query.Pageable;
import org.activiti.api.task.model.Task;
import org.activiti.api.task.runtime.TaskRuntime;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.IdentityLink;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.UUID;

@SpringBootTest(classes = Application.class)
@RunWith(SpringRunner.class)
public class ActivitiTest {
    @Autowired
    TaskRuntime taskRuntime;
    @Autowired
    ProcessRuntime processRuntime;
    @Autowired
    HistoryService historyService;
    @Autowired
    RepositoryService repositoryService;
    @Autowired
    ProcessEngine processEngine;
    Logger logger = LoggerFactory.getLogger(ActivitiTest.class);

    @Test
    public void testRepository() {
        List<Deployment> deployments = repositoryService.createDeploymentQuery()
                .list();
        List<ProcessDefinition> processDefinitions = repositoryService.createProcessDefinitionQuery().list();
        List<String> resources = repositoryService.getDeploymentResourceNames(deployments.get(0).getId());
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitions.get(0).getId());
        Model model = repositoryService.newModel();
        List<Model> models = repositoryService.createModelQuery().list();

        List<IdentityLink> identityLinks = repositoryService.getIdentityLinksForProcessDefinition(processDefinitions.get(0).getId());
        logger.info(deployments.toString());
    }

    @Test
    public void testProcessRunTime() {
        List<ProcessDefinition> definitions = repositoryService.createProcessDefinitionQuery().list();
        ProcessDefinition definition = definitions.get(0);

        StartProcessPayload startProcessPayload = ProcessPayloadBuilder.start().withProcessDefinitionId(definition.getId())
                .withProcessDefinitionKey(definition.getKey())
                .withBusinessKey(UUID.randomUUID().toString())
                .withName("hhhhh")
                .build();
        ProcessInstance processInstance = processRuntime.start(startProcessPayload);

        ProcessInstanceMeta processInstanceMeta = processRuntime.processInstanceMeta(processInstance.getId());
        processRuntime.signal(ProcessPayloadBuilder.signal()
                .withName("aaaaaa")
                .build()
        );
        Page<Task> taskPage = taskRuntime.tasks(Pageable.of(0, 100));

        logger.info(processInstance.toString());

    }


}
