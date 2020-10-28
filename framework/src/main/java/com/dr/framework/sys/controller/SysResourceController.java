package com.dr.framework.sys.controller;

import com.dr.framework.common.entity.ResultListEntity;
import com.dr.framework.common.entity.TreeNode;
import com.dr.framework.core.organise.entity.Person;
import com.dr.framework.core.security.bo.PermissionResource;
import com.dr.framework.core.security.bo.PermissionResourcePart;
import com.dr.framework.core.security.bo.ResourceProviderInfo;
import com.dr.framework.core.security.service.ResourceManager;
import com.dr.framework.core.util.Constants;
import com.dr.framework.core.web.annotations.Current;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 资源管理接口
 *
 * @author dr
 */
@RestController
@RequestMapping("${common.api-path:/api}/sysResource")
public class SysResourceController {
    @Autowired
    ResourceManager resourceManager;

    /**
     * 查询所有的资源类型
     *
     * @return
     */
    @PostMapping("resourceProvider")
    public ResultListEntity<ResourceProviderInfo> resourceProvider() {
        return ResultListEntity.success(resourceManager.getResourceProviders());
    }

    /**
     * 获取指定类型的资源分组
     *
     * @param type
     * @return
     */
    @PostMapping("resourceGroup")
    public ResultListEntity<? extends PermissionResource> resourceGroup(String type) {
        return ResultListEntity.success(resourceManager.getResourceGroup(type));
    }

    /**
     * 获取资源功能列表
     *
     * @param type
     * @return
     */
    @PostMapping("resourcePart")
    public ResultListEntity<PermissionResourcePart> resourcePart(String type) {
        return ResultListEntity.success(resourceManager.getResourceParts(type));
    }

    /**
     * 获取指定类型和分组的所有资源
     *
     * @param type
     * @param group
     * @return
     */
    @PostMapping("resourceTree")
    public ResultListEntity<TreeNode<? extends PermissionResource>> resourceTree(String type, @RequestParam(defaultValue = Constants.DEFAULT) String group) {
        return ResultListEntity.success(resourceManager.getResourcesTree(type, group));
    }

    /**
     * 获取指定人员指定类型和分组的带权限的资源
     *
     * @param person
     * @param type
     * @param group
     * @return
     */
    @PostMapping("personResource")
    public ResultListEntity<TreeNode<? extends PermissionResource>> personResource(
            @Current Person person,
            String type,
            @RequestParam(defaultValue = Constants.DEFAULT) String group) {
        return ResultListEntity.success(resourceManager.getPersonResourceTree(person.getId(), type, group));
    }

}
