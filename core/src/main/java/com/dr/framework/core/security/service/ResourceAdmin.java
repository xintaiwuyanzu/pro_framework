package com.dr.framework.core.security.service;

import com.dr.framework.core.security.bo.PermissionResource;
import com.dr.framework.core.security.bo.PermissionResourcePart;
import com.dr.framework.core.security.bo.ResourceProviderInfo;

import java.util.Collection;

/**
 * 统管没有权限判断的所有资源
 *
 * @author dr
 */
public interface ResourceAdmin {
    /**
     * 获取所有的资源提供器
     *
     * @return
     */
    Collection<ResourceProviderInfo> getResourceProviders();

    /**
     * 根据资源类型获取资源分组
     * 获取资源分组
     *
     * @param resourceType
     * @return
     */
    Collection<PermissionResource> getResourceGroup(String resourceType);

    /**
     * 获取资源功能项
     *
     * @param resourceType
     * @return
     */
    Collection<PermissionResourcePart> getResourceParts(String resourceType);

    /**
     * 获取所有资源列表
     *
     * @param resourceType
     * @param groupId
     * @return
     */
    Collection<PermissionResource> getResources(String resourceType, String groupId);
}
