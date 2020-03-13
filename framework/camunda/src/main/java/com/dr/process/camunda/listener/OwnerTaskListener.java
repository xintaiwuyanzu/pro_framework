package com.dr.process.camunda.listener;

import com.dr.framework.core.organise.entity.Person;
import com.dr.framework.core.organise.service.OrganisePersonService;
import com.dr.framework.core.process.service.ProcessService;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.engine.impl.identity.Authentication;
import org.camunda.bpm.engine.impl.util.ClockUtil;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import static com.dr.framework.core.process.service.ProcessService.OWNER_NAME_KEY;
import static com.dr.process.camunda.listener.OwnerTaskListener.BEAN_NAME;

/**
 * 用来设置任务创建人信息
 *
 * @author dr
 */
@Component(BEAN_NAME)
public class OwnerTaskListener implements TaskListener, ExecutionListener {
    public static final String BEAN_NAME = "OwnerTaskListener";
    private IdentityService identityService;
    private OrganisePersonService organisePersonService;

    public OwnerTaskListener(IdentityService identityService, OrganisePersonService organisePersonService) {
        this.identityService = identityService;
        this.organisePersonService = organisePersonService;
    }

    @Override
    public void notify(DelegateExecution execution) {
        String createPerson = (String) execution.getVariable(ProcessService.CREATE_KEY);
        if (!StringUtils.isEmpty(createPerson)) {
            Person person = organisePersonService.getPersonById(createPerson);
            execution.setVariable(ProcessService.CREATE_NAME_KEY, person.getUserName());
        }
        execution.setVariable(ProcessService.CREATE_DATE_KEY, ClockUtil.getCurrentTime().getTime());
    }

    @Override
    public void notify(DelegateTask delegateTask) {
        Authentication authentication = identityService.getCurrentAuthentication();
        if (authentication != null && !StringUtils.isEmpty(authentication.getUserId())) {
            Person person = organisePersonService.getPersonById(authentication.getUserId());
            delegateTask.setOwner(authentication.getUserId());
            if (person != null) {
                delegateTask.setVariableLocal(OWNER_NAME_KEY, person.getUserName());
            }
        }
        String assignee = delegateTask.getAssignee();
        if (!StringUtils.isEmpty(assignee)) {
            Person person = organisePersonService.getPersonById(assignee);
            if (person != null) {
                delegateTask.setVariableLocal(ProcessService.ASSIGNEE_NAME_KEY, person.getUserName());
            }
        }
    }

    public IdentityService getIdentityService() {
        return identityService;
    }

    public void setIdentityService(IdentityService identityService) {
        this.identityService = identityService;
    }

    public OrganisePersonService getOrganisePersonService() {
        return organisePersonService;
    }

    public void setOrganisePersonService(OrganisePersonService organisePersonService) {
        this.organisePersonService = organisePersonService;
    }

}
