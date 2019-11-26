package com.dr.process.camunda.service;

import com.dr.framework.common.dao.CommonMapper;
import com.dr.framework.common.entity.IdEntity;
import com.dr.framework.common.page.Page;
import com.dr.framework.core.organise.entity.Person;
import com.dr.framework.core.process.bo.ProPerty;
import com.dr.framework.core.process.bo.ProcessObject;
import com.dr.framework.core.process.bo.TaskObject;
import com.dr.framework.core.process.service.AbstractHandlerProcessService;
import com.dr.framework.core.process.service.ProcessHandler;
import com.dr.framework.core.process.service.ProcessService;
import org.camunda.bpm.engine.*;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.camunda.bpm.engine.history.HistoricTaskInstanceQuery;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.el.ExpressionManager;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.repository.ProcessDefinitionQuery;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.task.TaskQuery;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.StartEvent;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperties;
import org.camunda.bpm.model.xml.type.ModelElementType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * camunda 流程引擎默认实现
 *
 * @author dr
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class DefaultProcessService extends AbstractHandlerProcessService implements ProcessService {
    CommonMapper commonMapper;

    RuntimeService runtimeService;
    TaskService taskService;
    HistoryService historyService;
    RepositoryService repositoryService;
    ExpressionManager expressionManager;

    IdentityService identityService;

    public DefaultProcessService(List<ProcessHandler> handlers,
                                 CommonMapper commonMapper,
                                 RuntimeService runtimeService,
                                 RepositoryService repositoryService,
                                 TaskService taskService,
                                 HistoryService historyService,
                                 IdentityService identityService,
                                 ProcessEngineConfigurationImpl configuration) {
        super(handlers);
        this.commonMapper = commonMapper;
        this.runtimeService = runtimeService;
        this.taskService = taskService;
        this.historyService = historyService;
        this.repositoryService = repositoryService;
        this.identityService = identityService;
        this.expressionManager = configuration.getExpressionManager();

    }


    @Override
    protected TaskObject doStart(ProcessObject processObject,
                                 ProcessHandler processHandler,
                                 IdEntity formObject,
                                 String name,
                                 Map<String, Object> variMap,
                                 Person person) {
        if (variMap == null) {
            variMap = new HashMap<>();
        }
        variMap.put("formId", formObject.getId());
        variMap.put("title", name);

        ProPerty proPerty = processObject.getProPerty(FORM_URL_KEY);
        if (proPerty != null) {
            variMap.put(FORM_URL_KEY, proPerty.getValue());
        }
        runtimeService.startProcessInstanceById(processObject.getId(), variMap);
        return null;
    }

    @Override
    public TaskObject update(TaskObject taskObject, Map<String, Object> formMap, Map<String, Object> variMap, Person person) {
        Assert.isTrue(!StringUtils.isEmpty(taskObject.getId()), "任务id不能为空！");
        Task task = taskService.createTaskQuery()
                .taskId(taskObject.getId())
                .singleResult();
        ProcessDefinition processDefinition = repositoryService.getProcessDefinition(task.getProcessDefinitionId());
        ProcessHandler processHandler = getProcessHandler(processDefinition.getKey());

        IdEntity formObject = newFormObject(processHandler, formMap);
        processHandler.handleUpdate(formObject, taskObject, variMap, person);
        //taskService.setVariablesLocal(taskObject.getId(), variMap);
        taskService.complete(taskObject.getId(), variMap);
        return convertTask(taskService.createTaskQuery()
                .taskId(taskObject.getId())
                .initializeFormKeys()
                .singleResult());
    }

    @Override
    public Page<TaskObject> getTask(TaskObject query, int pageIndex, int pageSize) {
        TaskQuery taskQuery = buildQuery(query);
        long count = taskQuery.count();
        List<TaskObject> taskObjects = null;
        if (count > 0) {
            taskObjects = taskQuery.listPage(pageIndex * pageSize, (pageIndex + 1) * pageSize)
                    .stream()
                    .map(this::convertTask)
                    .collect(Collectors.toList());
        }
        return new Page(pageIndex, pageSize, count, taskObjects);
    }

    @Override
    public Page<TaskObject> getHistoryTask(TaskObject query, int pageIndex, int pageSize) {
        HistoricTaskInstanceQuery hQuery = buildHistoricQuery(query);
        long count = hQuery.count();
        List<TaskObject> taskObjects = null;
        if (count > 0) {
            taskObjects = hQuery.listPage(pageIndex * pageSize, (pageIndex + 1) * pageSize)
                    .stream()
                    .map(this::convertHistoryTask)
                    .collect(Collectors.toList());
        }
        return new Page<>(pageIndex, pageSize, count, taskObjects);
    }


    @Override
    public List<TaskObject> getTask(TaskObject query) {
        TaskQuery taskQuery = buildQuery(query);
        return taskQuery.list()
                .stream()
                .map(this::convertTask)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskObject> getHistoryTask(TaskObject query) {
        HistoricTaskInstanceQuery hQuery = buildHistoricQuery(query);
        return hQuery.list()
                .stream()
                .map(this::convertHistoryTask)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ProcessObject> processDefinition(ProcessObject processObject, int pageIndex, int pageSize) {
        ProcessDefinitionQuery query = buildQuery(processObject);
        long count = query.count();
        List<ProcessObject> processObjects = null;
        if (count > 0) {
            processObjects = query.listPage(pageIndex * pageSize, (pageIndex + 1) * pageSize)
                    .stream()
                    .map(this::convert)
                    .collect(Collectors.toList());
        }
        return new Page(pageIndex, pageSize, count, processObjects);
    }


    @Override
    public List<ProcessObject> processDefinition(ProcessObject processObject) {
        ProcessDefinitionQuery query = buildQuery(processObject);
        return query.list()
                .stream()
                .map(this::convert)
                .collect(Collectors.toList());
    }

    @Override
    public TaskObject taskDetail(String id) {
        Assert.isTrue(!StringUtils.isEmpty(id), "任务id不能为空！");
        Task task = taskService.createTaskQuery().taskId(id).initializeFormKeys().singleResult();
        return convertTask(task);
    }

    private TaskObject convertTask(Task task) {
        if (task == null) {
            return null;
        }
        TaskObject to = new TaskObject();
        Map<String, Object> variables = taskService.getVariables(task.getId());

        to.setAssignee(task.getAssignee());
        to.setAssigneeName((String) variables.get(ASSIGNEE_NAME_KEY));
        variables.remove(ASSIGNEE_NAME_KEY);

        to.setCreateDate(task.getCreateTime().getTime());
        to.setFormKey(task.getFormKey());
        to.setId(task.getId());
        to.setProcessInstanceId(task.getProcessInstanceId());
        to.setProcessDefineId(task.getProcessDefinitionId());
        to.setTaskDefineKey(task.getTaskDefinitionKey());
        to.setOwner(task.getOwner());

        to.setOwnerName((String) variables.get(OWNER_NAME_KEY));
        variables.remove(OWNER_NAME_KEY);

        to.setCreatePerson((String) variables.get(CREATE_KEY));
        variables.remove(CREATE_KEY);

        to.setCreateDate((Long) variables.get(CREATE_DATE_KEY));
        variables.remove(CREATE_DATE_KEY);

        to.setCreatePersonName((String) variables.get(CREATE_NAME_KEY));
        variables.remove(CREATE_NAME_KEY);

        to.setName(task.getName());
        to.setDescription(task.getDescription());
        to.setSuspend(task.isSuspended());


        to.setVariables(variables);
        return to;
    }

    private TaskObject convertHistoryTask(HistoricTaskInstance history) {
        if (history == null) {
            return null;
        }
        TaskObject to = new TaskObject();
        to.setAssignee(history.getAssignee());
        to.setId(history.getId());
        to.setCreateDate(history.getStartTime().getTime());
        to.setProcessInstanceId(history.getProcessInstanceId());
        to.setProcessDefineId(history.getProcessDefinitionId());
        to.setTaskDefineKey(history.getTaskDefinitionKey());
        to.setOwner(history.getOwner());
        to.setName(history.getName());
        to.setDescription(history.getDescription());
        to.setForm(historyService.createHistoricVariableInstanceQuery().taskIdIn(history.getId()).singleResult());
        return to;
    }

    private ProcessObject convert(ProcessDefinition processDefinition) {
        return convert(processDefinition, true);
    }

    private ProcessObject convert(ProcessDefinition processDefinition, boolean withProperty) {
        if (processDefinition == null) {
            return null;
        }
        ProcessObject po = new ProcessObject();
        po.setName(processDefinition.getName());
        po.setDescription(processDefinition.getDescription());
        po.setKey(processDefinition.getKey());
        po.setId(processDefinition.getId());
        po.setVersion(processDefinition.getVersion());
        if (withProperty) {
            po.setProPerties(getProcessDefineProperty(processDefinition.getId()));
        }
        return po;
    }

    private List<ProPerty> getProcessDefineProperty(String processDefineId) {
        //读取定义的扩展属性c
        BpmnModelInstance bpmnModelInstance = repositoryService.getBpmnModelInstance(processDefineId);
        Process process = bpmnModelInstance.getModelElementsByType(Process.class).iterator().next();

        Collection<StartEvent> startEvents = bpmnModelInstance.getModelElementsByType(StartEvent.class);

        Collection<CamundaProperties> properties = process.getExtensionElements()
                .getElementsQuery()
                .filterByType(CamundaProperties.class)
                .list();
        if (!properties.isEmpty()) {
            List<ProPerty> proPerties = new ArrayList<>();
            properties.stream()
                    .forEach(p -> {
                        p.getCamundaProperties()
                                .stream()
                                .forEach(pp -> {
                                    ProPerty proPerty = new ProPerty();
                                    proPerty.setId(pp.getCamundaId());
                                    proPerty.setName(pp.getCamundaName());
                                    proPerty.setValue(pp.getCamundaValue());
                                    proPerties.add(proPerty);
                                });
                    });
            return proPerties;
        }
        return null;
    }

    private ProcessDefinitionQuery buildQuery(ProcessObject processObject) {
        ProcessDefinitionQuery query = repositoryService.createProcessDefinitionQuery();
        if (processObject != null) {
            if (!StringUtils.isEmpty(processObject.getName())) {
                query.processDefinitionNameLike(processObject.getName());
            }
            if (!StringUtils.isEmpty(processObject.getKey())) {
                query.processDefinitionKeyLike(processObject.getKey());
            }
            if (!StringUtils.isEmpty(processObject.getVersion())) {
                query.versionTagLike(String.valueOf(processObject.getVersion()));
            }
        }
        query.active()
                .latestVersion()
                .orderByProcessDefinitionName()
                .asc();
        return query;
    }

    private TaskQuery buildQuery(TaskObject taskObject) {
        TaskQuery taskQuery = taskService.createTaskQuery();
        if (taskObject != null) {
            if (!StringUtils.isEmpty(taskObject.getName())) {
                taskQuery.taskNameLike(taskObject.getName());
            }
            if (!StringUtils.isEmpty(taskObject.getProcessDefineId())) {
                taskQuery.processDefinitionId(taskObject.getProcessDefineId());
            }
            if (!StringUtils.isEmpty(taskObject.getProcessInstanceId())) {
                taskQuery.processInstanceId(taskObject.getProcessInstanceId());
            }
            if (!StringUtils.isEmpty(taskObject.getAssignee())) {
                taskQuery.taskAssigneeLike(taskObject.getAssignee());
            }
        }
        taskQuery.initializeFormKeys()
                .active()
                .orderByTaskCreateTime()
                .asc();
        return taskQuery;
    }


    protected BpmnModelInstance getModelInstanceByKey(String processDefinitionKey) {
        Assert.isTrue(!StringUtils.isEmpty(processDefinitionKey), "流程定义编码不能为空！");
        String processId = repositoryService.createProcessDefinitionQuery().processDefinitionKey(processDefinitionKey).latestVersion().singleResult().getId();
        return repositoryService.getBpmnModelInstance(processId);
    }

    private StartEvent getStartEvent(BpmnModelInstance modelInstance) {
        ModelElementType startEventType = modelInstance.getModel().getType(StartEvent.class);
        return (StartEvent) modelInstance.getModelElementsByType(startEventType).iterator().next();
    }


    private HistoricTaskInstanceQuery buildHistoricQuery(TaskObject taskObject) {
        HistoricTaskInstanceQuery hQuery = historyService.createHistoricTaskInstanceQuery();
        if (!StringUtils.isEmpty(taskObject.getName())) {
            hQuery.taskNameLike(taskObject.getName());
        }
        if (!StringUtils.isEmpty(taskObject.getProcessDefineId())) {
            hQuery.processDefinitionId(taskObject.getProcessDefineId());
        }
        if (!StringUtils.isEmpty(taskObject.getProcessInstanceId())) {
            hQuery.processInstanceId(taskObject.getProcessInstanceId());
        }
        if (!StringUtils.isEmpty(taskObject.getAssignee())) {
            hQuery.taskAssigneeLike(taskObject.getAssignee());
        }
        hQuery.finished()
                .orderByHistoricActivityInstanceStartTime()
                .asc();
        return hQuery;
    }

    @Override
    protected ProcessObject getProcessById(String id) {
        Assert.isTrue(!StringUtils.isEmpty(id), "流程定义id不能为空！");
        ProcessDefinition processDefinition = repositoryService.getProcessDefinition(id);
        return convert(processDefinition, true);
    }

}
