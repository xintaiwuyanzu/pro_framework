package com.dr.process.camunda.impl;

import com.dr.framework.core.organise.service.LoginService;
import com.dr.framework.core.organise.service.OrganisePersonService;
import com.dr.framework.core.security.service.SecurityManager;
import org.camunda.bpm.engine.filter.Filter;
import org.camunda.bpm.engine.identity.Group;
import org.camunda.bpm.engine.identity.Tenant;
import org.camunda.bpm.engine.identity.User;
import org.camunda.bpm.engine.impl.cfg.auth.ResourceAuthorizationProvider;
import org.camunda.bpm.engine.impl.persistence.entity.AuthorizationEntity;
import org.camunda.bpm.engine.repository.DecisionDefinition;
import org.camunda.bpm.engine.repository.DecisionRequirementsDefinition;
import org.camunda.bpm.engine.repository.Deployment;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.springframework.stereotype.Component;

/**
 * 适用平台权限管理
 */
@Component
public class DefaultResourceAuthorizationProvider implements ResourceAuthorizationProvider {
    private SecurityManager securityManager;
    private OrganisePersonService organisePersonService;
    private LoginService loginService;

    public DefaultResourceAuthorizationProvider(SecurityManager securityManager,
                                                OrganisePersonService organisePersonService,
                                                LoginService loginService) {
        this.securityManager = securityManager;
        this.organisePersonService = organisePersonService;
        this.loginService = loginService;
    }

    @Override
    public AuthorizationEntity[] newUser(User user) {
        return new AuthorizationEntity[0];
    }

    @Override
    public AuthorizationEntity[] newGroup(Group group) {
        return new AuthorizationEntity[0];
    }

    @Override
    public AuthorizationEntity[] newTenant(Tenant tenant) {
        return new AuthorizationEntity[0];
    }

    @Override
    public AuthorizationEntity[] groupMembershipCreated(String groupId, String userId) {
        return new AuthorizationEntity[0];
    }

    @Override
    public AuthorizationEntity[] tenantMembershipCreated(Tenant tenant, User user) {
        return new AuthorizationEntity[0];
    }

    @Override
    public AuthorizationEntity[] tenantMembershipCreated(Tenant tenant, Group group) {
        return new AuthorizationEntity[0];
    }

    @Override
    public AuthorizationEntity[] newFilter(Filter filter) {
        return new AuthorizationEntity[0];
    }

    @Override
    public AuthorizationEntity[] newDeployment(Deployment deployment) {
        return new AuthorizationEntity[0];
    }

    @Override
    public AuthorizationEntity[] newProcessDefinition(ProcessDefinition processDefinition) {
        return new AuthorizationEntity[0];
    }

    @Override
    public AuthorizationEntity[] newProcessInstance(ProcessInstance processInstance) {
        return new AuthorizationEntity[0];
    }

    @Override
    public AuthorizationEntity[] newTask(Task task) {
        return new AuthorizationEntity[0];
    }

    @Override
    public AuthorizationEntity[] newTaskAssignee(Task task, String oldAssignee, String newAssignee) {
        return new AuthorizationEntity[0];
    }

    @Override
    public AuthorizationEntity[] newTaskOwner(Task task, String oldOwner, String newOwner) {
        return new AuthorizationEntity[0];
    }

    @Override
    public AuthorizationEntity[] newTaskUserIdentityLink(Task task, String userId, String type) {
        return new AuthorizationEntity[0];
    }

    @Override
    public AuthorizationEntity[] newTaskGroupIdentityLink(Task task, String groupId, String type) {
        return new AuthorizationEntity[0];
    }

    @Override
    public AuthorizationEntity[] deleteTaskUserIdentityLink(Task task, String userId, String type) {
        return new AuthorizationEntity[0];
    }

    @Override
    public AuthorizationEntity[] deleteTaskGroupIdentityLink(Task task, String groupId, String type) {
        return new AuthorizationEntity[0];
    }

    @Override
    public AuthorizationEntity[] newDecisionDefinition(DecisionDefinition decisionDefinition) {
        return new AuthorizationEntity[0];
    }

    @Override
    public AuthorizationEntity[] newDecisionRequirementsDefinition(DecisionRequirementsDefinition decisionRequirementsDefinition) {
        return new AuthorizationEntity[0];
    }
}
