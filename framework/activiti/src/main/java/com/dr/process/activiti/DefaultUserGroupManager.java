package com.dr.process.activiti;

import com.dr.framework.core.organise.service.OrganisePersonService;
import org.activiti.api.runtime.shared.identity.UserGroupManager;
import org.activiti.api.runtime.shared.security.SecurityManager;

import java.util.List;

/**
 * activiti使用的用户管理
 *
 * @author dr
 */
public class DefaultUserGroupManager implements UserGroupManager {
    private SecurityManager securityManager;
    private OrganisePersonService organisePersonService;

    public DefaultUserGroupManager(SecurityManager securityManager, OrganisePersonService organisePersonService) {
        this.securityManager = securityManager;
        this.organisePersonService = organisePersonService;
    }

    @Override
    public List<String> getUserGroups(String username) {
        return null;
    }

    @Override
    public List<String> getUserRoles(String username) {
        return null;
    }

    @Override
    public List<String> getGroups() {
        return null;
    }

    @Override
    public List<String> getUsers() {
        return null;
    }
}
