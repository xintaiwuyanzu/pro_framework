package com.dr.process.camunda.command.process;

import com.dr.framework.core.process.bo.ProPerty;
import org.camunda.bpm.engine.impl.interceptor.CommandContext;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.BaseElement;
import org.camunda.bpm.model.bpmn.instance.ExtensionElements;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperties;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.dr.framework.core.process.service.ProcessService.*;

/**
 * @author dr
 */
public abstract class AbstractGetProcessDefinitionCmd {
    private boolean withProperty;
    private boolean withStartUser;
    static final List<String> CUSTOM_KEYS = Arrays.asList(CREATE_KEY, CREATE_NAME_KEY, CREATE_DATE_KEY,

            OWNER_KEY, OWNER_NAME_KEY,

            ASSIGNEE_KEY, ASSIGNEE_NAME_KEY,

            TITLE_KEY,

            FORM_URL_KEY
    );


    public AbstractGetProcessDefinitionCmd(boolean withProperty) {
        this.withProperty = withProperty;
    }

    public AbstractGetProcessDefinitionCmd(boolean withProperty, boolean withStartUser) {
        this.withProperty = withProperty;
        this.withStartUser = withStartUser;
    }

    protected com.dr.framework.core.process.bo.ProcessDefinition convert(org.camunda.bpm.engine.repository.ProcessDefinition processDefinition
            , CommandContext commandContext
    ) {
        if (processDefinition == null) {
            return null;
        }
        com.dr.framework.core.process.bo.ProcessDefinition po = new com.dr.framework.core.process.bo.ProcessDefinition();
        po.setName(processDefinition.getName());
        po.setDescription(processDefinition.getDescription());
        po.setType(processDefinition.getKey());
        po.setId(processDefinition.getId());
        po.setVersion(processDefinition.getVersion());
        if (withProperty) {
            po.setProPerties(getProperty(processDefinition.getId(),
                    processDefinition.getKey(),
                    commandContext));
        }
        if (withStartUser) {
           /* BpmnModelInstance bpmnModelInstance = commandContext.getProcessEngineConfiguration().getRepositoryService().getBpmnModelInstance(processDefinition.getId());
            String candidateUsersString = bpmnModelInstance.getModelElementById(processDefinition.getKey()).getAttributeValueNs(CAMUNDA_BPMN_EXTENSIONS_NS.getNamespaceUri(), "candidateStarterUsers");
            if (!StringUtils.isEmpty(candidateUsersString)) {
                Expression expression = commandContext.getProcessEngineConfiguration()
                        .getExpressionManager()
                        .createExpression(candidateUsersString);
                Object o = expression.getValue(StartProcessVariableScope.getSharedInstance());
            }*/
        }
        return po;
    }

    public static List<ProPerty> getProperty(String processDefineId, String key, CommandContext commandContext) {
        BpmnModelInstance bpmnModelInstance = commandContext.getProcessEngineConfiguration().getRepositoryService().getBpmnModelInstance(processDefineId);
        BaseElement element = bpmnModelInstance.getModelElementById(key);

        ExtensionElements extensionElements = element.getExtensionElements();
        if (extensionElements != null) {
            return extensionElements
                    .getElementsQuery()
                    .filterByType(CamundaProperties.class)
                    .list()
                    .stream()
                    .map(CamundaProperties::getCamundaProperties)
                    .reduce((l, cs) -> {
                                l.addAll(cs);
                                return l;
                            }
                    )
                    .get()
                    .stream()
                    .map(p -> {
                        ProPerty proPerty = new ProPerty();
                        proPerty.setId(p.getCamundaId());
                        proPerty.setName(p.getCamundaName());
                        proPerty.setValue(p.getCamundaValue());
                        return proPerty;
                    })
                    .collect(Collectors.toList());
        }
        return null;
    }

    public static Map<String, Object> filter(Map<String, Object> source) {
        return source.entrySet()
                .stream()
                .filter(e -> !CUSTOM_KEYS.contains(e.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
