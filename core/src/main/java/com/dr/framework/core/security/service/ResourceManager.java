package com.dr.framework.core.security.service;

import com.dr.framework.common.entity.TreeNode;
import com.dr.framework.core.security.bo.PermissionResource;
import com.dr.framework.core.util.Constants;

import java.util.List;

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
    default List<? extends PermissionResource> getPersonResources(String userId, String resourceType) {
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
    List<? extends PermissionResource> getPersonResources(String userId, String resourceType, String groupId);

    /**
     * 获取指定人员的资源树
     *
     * @param userId
     * @param resourceType
     * @return
     */
    default List<TreeNode<? extends PermissionResource>> getPersonResourceTree(String userId, String resourceType) {
        return getPersonResourceTree(userId, resourceType, Constants.DEFAULT);
    }

    /**
     * 获取特定用户资源树
     *
     * @param userId
     * @param resourceType
     * @param groupId
     * @return
     */
    List<TreeNode<? extends PermissionResource>> getPersonResourceTree(String userId, String resourceType, String groupId);

}
