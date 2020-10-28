package com.dr.framework.core.security.service;

import com.dr.framework.common.entity.TreeNode;
import com.dr.framework.core.security.bo.PermissionResource;
import com.dr.framework.core.security.bo.PermissionResourcePart;
import com.dr.framework.core.security.bo.ResourceProviderInfo;
import com.dr.framework.core.util.Constants;

import java.util.List;

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
    List<ResourceProviderInfo> getResourceProviders();

    /**
     * 根据资源类型获取资源分组
     * 获取资源分组
     *
     * @param resourceType
     * @return
     */
    List<? extends PermissionResource> getResourceGroup(String resourceType);

    /**
     * 获取资源功能项
     *
     * @param resourceType
     * @return
     */
    List<PermissionResourcePart> getResourceParts(String resourceType);

    /**
     * 获取指定类型的资源列表
     *
     * @param resourceType
     * @return
     */
    default List<? extends PermissionResource> getResources(String resourceType) {
        return getResources(resourceType, Constants.DEFAULT);
    }

    /**
     * 获取所有资源列表
     *
     * @param resourceType
     * @param groupId
     * @return
     */
    List<? extends PermissionResource> getResources(String resourceType, String groupId);

    /**
     * 获取资源树
     *
     * @param resourceType
     * @return
     */
    default List<TreeNode<? extends PermissionResource>> getResourcesTree(String resourceType) {
        return getResourcesTree(resourceType, Constants.DEFAULT);
    }

    /**
     * 获取资源树
     *
     * @param resourceType
     * @param groupId
     * @return
     */
    List<TreeNode<? extends PermissionResource>> getResourcesTree(String resourceType, String groupId);
}
