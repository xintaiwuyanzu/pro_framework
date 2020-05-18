package com.dr.process.activiti.service;

import com.dr.framework.common.dao.CommonMapper;
import com.dr.framework.common.entity.IdEntity;
import com.dr.framework.common.service.CommonService;
import com.dr.framework.core.organise.entity.Person;
import com.dr.framework.core.process.bo.ProcessObject;
import com.dr.framework.core.process.bo.TaskObject;
import com.dr.framework.core.process.service.AbstractHandlerProcessService;
import com.dr.framework.core.process.service.ProcessHandler;
import org.activiti.api.process.model.ProcessInstance;
import org.activiti.api.process.model.builders.ProcessPayloadBuilder;
import org.activiti.api.process.runtime.ProcessAdminRuntime;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.runtime.shared.query.Page;
import org.activiti.api.runtime.shared.query.Pageable;
import org.activiti.api.task.model.Task;
import org.activiti.api.task.model.builders.TaskPayloadBuilder;
import org.activiti.api.task.runtime.TaskAdminRuntime;
import org.activiti.api.task.runtime.TaskRuntime;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 默认流程管理器
 *
 * @author dr
 */
@Service("DefaultProcessService")
@Transactional(rollbackFor = Exception.class)
public class DefaultProcessService extends AbstractHandlerProcessService implements TaskListener {
    Logger logger = LoggerFactory.getLogger(DefaultProcessService.class);

    final CommonService commonService;
    final CommonMapper commonMapper;
    final RepositoryService repositoryService;
    final TaskRuntime taskRuntime;
    final TaskAdminRuntime taskAdminRuntime;
    final ProcessRuntime processRuntime;
    final ProcessAdminRuntime processAdminRuntime;
    final HistoryService historyService;

    public DefaultProcessService(List<ProcessHandler> handlers,
                                 CommonService commonService,
                                 CommonMapper commonMapper,
                                 RepositoryService repositoryService,
                                 TaskRuntime taskRuntime,
                                 TaskAdminRuntime taskAdminRuntime,
                                 ProcessRuntime processRuntime,
                                 ProcessAdminRuntime processAdminRuntime,
                                 HistoryService historyService) {
        super(handlers);
        this.commonService = commonService;
        this.commonMapper = commonMapper;

        this.repositoryService = repositoryService;
        this.taskRuntime = taskRuntime;
        this.taskAdminRuntime = taskAdminRuntime;
        this.processRuntime = processRuntime;
        this.processAdminRuntime = processAdminRuntime;
        this.historyService = historyService;
    }


    @Override
    protected TaskObject doStart(ProcessObject processObject, ProcessHandler processHandler, Object formObject, String name, Map<String, Object> variMap, Person person) {
        ProcessInstance processInstance = processRuntime.start(ProcessPayloadBuilder.start()
                .withVariables(variMap)
                .withProcessDefinitionKey(processObject.getKey())
                .withBusinessKey(((IdEntity) formObject).getId())
                .withName(name)
                .build());
        updateTaskTitle(name, processInstance.getId());
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processInstance.getProcessDefinitionId());


        Page<Task> tasks = taskRuntime.tasks(Pageable.of(0, 100),
                TaskPayloadBuilder.tasks()
                        .withProcessInstanceId(processInstance.getId())
                        .build()
        );

        taskRuntime.complete(TaskPayloadBuilder
                .complete()
                .withTaskId(tasks.getContent().get(0).getId())
                .withVariables(variMap)
                .build());
        List<HistoricProcessInstance> historicProcessInstances = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstance.getId()).list();
        List<HistoricActivityInstance> historicActivityInstances = historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstance.getId()).list();
        List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstance.getId()).list();
        return null;
    }

    void updateTaskTitle(String title, String processInstanceId) {
        Page<Task> taskPage = taskAdminRuntime.tasks(Pageable.of(0, 100),
                TaskPayloadBuilder
                        .tasks()
                        .withProcessInstanceId(processInstanceId)
                        .build());
        for (Task task : taskPage.getContent()) {
            String old = task.getName();
            if (!old.contains(title)) {
                old = title + old;
            }
            taskAdminRuntime.update(TaskPayloadBuilder.update()
                    .withName(old)
                    .withTaskId(task.getId())
                    .build());
        }
    }


    @Override
    public TaskObject update(TaskObject taskObject, Map<String, Object> formMap, Map<String, Object> variMap, Person person) {
        return null;
    }


    @Override
    public List getTask() {
        return null;
    }

    @Override
    public void notify(DelegateTask delegateTask) {
        logger.info(delegateTask.getName());
        delegateTask.setAssignee((String) delegateTask.getVariable("assignee"));
    }
}
