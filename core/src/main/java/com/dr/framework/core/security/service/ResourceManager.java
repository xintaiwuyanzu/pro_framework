package com.dr.framework.core.security.service;

import com.dr.framework.core.security.bo.PermissionResource;
import com.dr.framework.core.util.Constants;

import java.util.Collection;

/**
 * 系统权限资源管理器
 *
 * @author dr
 */
public interface ResourceManager extends ResourceAdmin {
    /**
     * 获取特定用户的资源列表
     *
     * @param userId
     * @param resourceType
     * @return
     */
    default Collection<PermissionResource> getPersonResources(String userId, String resourceType) {
        return getPersonResources(userId, resourceType, Constants.DEFAULT);
    }

    /**
     * 获取特定用户的资源列表
     *
     * @param userId
     * @param resourceType
     * @param groupId
     * @return
     */
    Collection<PermissionResource> getPersonResources(String userId, String resourceType, String groupId);

}
