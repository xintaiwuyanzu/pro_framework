package com.dr.process.camunda.parselistener;

import com.dr.framework.core.process.service.ProcessService;
import com.dr.process.camunda.listener.OwnerTaskListener;
import org.camunda.bpm.engine.delegate.DelegateListener;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.camunda.bpm.engine.impl.bpmn.listener.DelegateExpressionExecutionListener;
import org.camunda.bpm.engine.impl.bpmn.parser.AbstractBpmnParseListener;
import org.camunda.bpm.engine.impl.bpmn.parser.BpmnParse;
import org.camunda.bpm.engine.impl.el.Expression;
import org.camunda.bpm.engine.impl.el.ExpressionManager;
import org.camunda.bpm.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.camunda.bpm.engine.impl.pvm.process.ActivityImpl;
import org.camunda.bpm.engine.impl.pvm.process.ProcessDefinitionImpl;
import org.camunda.bpm.engine.impl.pvm.process.ScopeImpl;
import org.camunda.bpm.engine.impl.task.TaskDefinition;
import org.camunda.bpm.engine.impl.task.listener.DelegateExpressionTaskListener;
import org.camunda.bpm.engine.impl.util.xml.Element;
import org.springframework.util.StringUtils;

/**
 * @author dr
 */
public class CustomerEventListenerParser extends AbstractBpmnParseListener {
    Expression listenerExpression;
    Expression assigneeExpression;
    Expression descriptionExpression;

    public CustomerEventListenerParser(ExpressionManager expressionManager) {
        listenerExpression = expressionManager.createExpression("${" + OwnerTaskListener.BEAN_NAME + "}");
        assigneeExpression = expressionManager.createExpression("${assignee}");
        descriptionExpression = expressionManager.createExpression("${title}");
    }

    @Override
    public void parseProcess(Element processElement, ProcessDefinitionEntity processDefinition) {
        super.parseProcess(processElement, processDefinition);

        DelegateListener delegateListener = new DelegateExpressionExecutionListener(listenerExpression, null);
        processDefinition.addListener(ExecutionListener.EVENTNAME_START, delegateListener);
    }

    @Override
    public void parseStartEvent(Element startEventElement, ScopeImpl scope, ActivityImpl startEventActivity) {
        super.parseStartEvent(startEventElement, scope, startEventActivity);
        ProcessDefinitionImpl processDefinition = startEventActivity.getProcessDefinition();
        String init = (String) processDefinition.getProperty(BpmnParse.PROPERTYNAME_INITIATOR_VARIABLE_NAME);
        if (StringUtils.isEmpty(init)) {
            processDefinition.setProperty(BpmnParse.PROPERTYNAME_INITIATOR_VARIABLE_NAME, ProcessService.CREATE_KEY);
        }
    }

    @Override
    public void parseUserTask(Element userTaskElement, ScopeImpl scope, ActivityImpl activity) {
        super.parseUserTask(userTaskElement, scope, activity);
        UserTaskActivityBehavior activityBehavior = (UserTaskActivityBehavior) activity.getActivityBehavior();
        TaskDefinition taskDefinition = activityBehavior.getTaskDefinition();
        if (taskDefinition.getAssigneeExpression() == null) {
            taskDefinition.setAssigneeExpression(assigneeExpression);
        }
        TaskListener delegateListener = new DelegateExpressionTaskListener(listenerExpression, null);
        taskDefinition.addTaskListener(TaskListener.EVENTNAME_CREATE, delegateListener);

        if (taskDefinition.getDescriptionExpression() == null) {
            taskDefinition.setDescriptionExpression(descriptionExpression);
        }

    }
}
