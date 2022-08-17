package com.dr.framework.core.security.service;

import com.dr.framework.core.security.bo.PermissionResource;
import com.dr.framework.core.security.bo.PermissionResourcePart;

import java.util.Collections;
import java.util.List;

/**
 * 动态提供权限资源数据
 * 根据获取到的数据动态拼凑成权限字符串
 * 把权限字符串关联到角色上
 *
 * @author dr
 */
public interface ResourceProvider {
    /**
     * 资源可以分组
     *
     * @return
     */
    default List<? extends PermissionResource> getGroupResource() {
        return Collections.emptyList();
    }

    /**
     * 根据分组Id获取所有的资源数据
     *
     * @param groupId
     * @return
     */
    List<? extends PermissionResource> getResources(String groupId);

    /**
     * 当没有配置权限的时候，可以通过业务逻辑提供默认权限功能
     *
     * @param personId
     * @param groupId
     * @return
     */
    default List<? extends PermissionResource> getResourcesWhenEmpty(String personId, String groupId) {
        return Collections.EMPTY_LIST;
    }

    /**
     * 根据主键获取一条资源
     *
     * @param resourceId
     * @return
     */
    PermissionResource getResource(String resourceId);

    /**
     * 获取改类型权限的具体功能
     * <p>
     * 比如：添加、删除、编辑、修改等等之类的
     *
     * @return
     */
    default List<PermissionResourcePart> getParts() {
        return Collections.emptyList();
    }

    /**
     * 获取资源类型
     *
     * @return
     */
    String getType();

    /**
     * 获取资源名称
     *
     * @return
     */
    String getName();

    /**
     * 获取描述
     *
     * @return
     */
    default String getDescription() {
        return "";
    }

    /**
     * 获取排序
     *
     * @return
     */
    default Integer getOrder() {
        return 0;
    }

}
