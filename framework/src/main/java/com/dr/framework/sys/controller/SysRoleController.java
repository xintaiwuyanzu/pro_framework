package com.dr.framework.sys.controller;

import com.dr.framework.common.controller.BaseServiceController;
import com.dr.framework.common.entity.ResultEntity;
import com.dr.framework.core.orm.sql.support.SqlQuery;
import com.dr.framework.core.security.entity.Role;
import com.dr.framework.core.security.service.SecurityManager;
import com.dr.framework.sys.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.dr.framework.common.entity.ResultEntity.success;

/**
 * 角色管理
 *
 * @author lc
 */
@RestController
@RequestMapping("${common.api-path:/api}/sysrole")
public class SysRoleController extends BaseServiceController<RoleService, Role> {
    @Autowired
    SecurityManager securityManager;

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
            //  securityManager.addMenuToRole(roleId, addMenus);
        }
        if (delMenus != null) {
            //securityManager.removeRoleMenu(roleId, delMenus);
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

    @Override
    protected SqlQuery<Role> buildPageQuery(HttpServletRequest request, Role entity) {
        //TODO
        return null;
    }
}
