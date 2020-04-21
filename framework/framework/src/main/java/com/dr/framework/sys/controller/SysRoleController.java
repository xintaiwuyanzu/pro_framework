package com.dr.framework.sys.controller;

import com.dr.framework.common.entity.ResultEntity;
import com.dr.framework.common.page.Page;
import com.dr.framework.core.organise.entity.Person;
import com.dr.framework.core.security.entity.Role;
import com.dr.framework.core.security.entity.SysMenu;
import com.dr.framework.core.security.query.RoleQuery;
import com.dr.framework.core.security.service.SecurityManager;
import com.dr.framework.core.web.annotations.Current;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.dr.framework.common.entity.ResultEntity.success;

/**
 * 角色管理
 * @author lc
 */
@RestController
@RequestMapping("${common.api-path:/api}/sysrole")
public class SysRoleController {

    @Autowired
    SecurityManager securityManager;

    /**
     * 添加新角色
     *
     * @param person
     * @param order
     * @param roleName
     * @param roleCode
     * @return
     */
    @RequestMapping("/addRole")
    public ResultEntity addRole(@Current Person person, int order, String roleName, String roleCode) {
        Role role = new Role();
        role.setCode(roleCode);
        role.setName(roleName);
        role.setId(UUID.randomUUID().toString());
        role.setCreateDate(System.currentTimeMillis());
        role.setCreatePerson(person.getUserName());
        role.setOrder(order);
        role.setStatus(0);
        securityManager.addRole(role);
        return success();
    }

    /**
     * 修改角色（角色名称等...）
     *
     * @param person
     * @param role
     * @return
     */
    @PostMapping(value = "/updateRole")
    public ResultEntity updateRole(@Current Person person, Role role) {
        role.setUpdatePerson(person.getUserName());
        role.setUpdateDate(System.currentTimeMillis());
        securityManager.updateRole(role);
        return success();
    }

    /**
     * 删除角色
     *
     * @param roleCode
     * @return
     */
    @RequestMapping("/deleteRole")
    public ResultEntity deleteRole(String roleCode) {
        securityManager.deleteRole(roleCode);
        return success();
    }

    /**
     * 分页查询角色列表
     *
     * @param page
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/page")
    public ResultEntity<Page<Role>> getRolePage(@RequestParam(defaultValue = "1") int page,
                                                @RequestParam(defaultValue = Page.DEFAULT_PAGE_SIZE + "15") int pageSize) {
        return success(securityManager.selectRolePage(new RoleQuery.Builder().build(), (page - 1) * pageSize, page * pageSize - 1));
    }

    /**
     * 根据角色ID查询角色详情
     *
     * @param roleId
     * @return
     */
    @RequestMapping("/getRoleById")
    public ResultEntity<Role> getRoleById(String roleId) {
        return success(securityManager.selectRoleList(new RoleQuery.Builder().idEqual(roleId).build()).get(0));
    }

    /**
     * 给角色加权限（多个权限/菜单）
     *
     * @param addPermissionIds
     * @param delPermissionIds
     * @param roleId
     * @param addMenuIds
     * @param delMenuIds
     * @return
     */
    @RequestMapping("/addPermissionToRole")
    public ResultEntity addPermissionToRole(String addPermissionIds,//添加的权限集合
                                            String delPermissionIds,//删除的权限集合
                                            String roleId,
                                            String addMenuIds,//添加的菜单集合
                                            String delMenuIds//删除的菜单集合
    ) {
        Assert.isTrue(!StringUtils.isEmpty(roleId), "角色不存在或为空！");
        String[] addPermissions = getArray(addPermissionIds);
        String[] delPermissions = getArray(delPermissionIds);
        String[] addMenus = getArray(addMenuIds);
        String[] delMenus = getArray(delMenuIds);
        if (addPermissions != null) {
            securityManager.addPermissionToRole(roleId, addPermissions);
        }
        if (delPermissions != null) {
            securityManager.removeRolePermission(roleId, delPermissions);
        }
        if (addMenus != null) {
            securityManager.addMenuToRole(roleId, addMenus);
        }
        if (delMenus != null) {
            securityManager.removeRoleMenu(roleId, delMenus);
        }
        return success();
    }

    private String[] getArray(String s) {
        if (!StringUtils.isEmpty(s)) {
            return s.split(",");
        }
        return null;
    }


    /**
     * 用户添加角色
     *
     * @param addRoleIds
     * @param delRoleIds
     * @param userId
     * @return
     */
    @RequestMapping("/addRoleToUser")
    public ResultEntity addRoleToUser(String addRoleIds, String delRoleIds, String userId) {
        Assert.isTrue(!StringUtils.isEmpty(userId), "用户为空或不存在");
        String[] addRoles = getArray(addRoleIds);
        String[] delRoles = getArray(delRoleIds);
        if (delRoles != null) {
            securityManager.removeUserRole(userId, delRoles);
        }
        if (addRoles != null) {
            securityManager.addRoleToUser(userId, addRoles);
        }
        return success();
    }

    /**
     * 获取用户角色列表
     *
     * @param userId
     * @return
     */
    @RequestMapping("/getRoleListToUser")
    public ResultEntity<List<Role>> getRoleListToUser(String userId) {
        return success(securityManager.userRoles(userId));
    }

    /**
     * 获取角色列表，根据用户ID标注哪些角色已授权给该用户
     *
     * @param userId
     * @return
     */
    @RequestMapping("/getRoleList")
    public ResultEntity<Map> getRoleList(String userId) {
        return success(securityManager.getRoleList(userId));
    }

    /**
     * 获取权限列表和菜单列表，根据角色ID标注哪些权限已授权给该角色
     *
     * @param sysMenu
     * @return
     */
    @RequestMapping("/getPermissionMenuList")
    public ResultEntity<List> getPermissionMenuList(@Current Person person, SysMenu sysMenu) {
        return success(securityManager.getPermissionMenuList(person, sysMenu));
    }

    /**
     *
     *
     * @param person
     * @param roleId
     * @param type
     * @return
     */
    @RequestMapping("/getPermissionMenuListOne")
    public ResultEntity<List> getPermissionMenuListOne(@Current Person person, String roleId, String type) {
        return success(securityManager.getPermissionMenuListOne(person, roleId, type));
    }

}
