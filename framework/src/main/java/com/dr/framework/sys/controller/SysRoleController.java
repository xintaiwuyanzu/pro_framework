package com.dr.framework.sys.controller;

import com.dr.framework.common.controller.BaseServiceController;
import com.dr.framework.common.entity.BaseEntity;
import com.dr.framework.common.entity.ResultEntity;
import com.dr.framework.common.entity.ResultListEntity;
import com.dr.framework.core.orm.sql.support.SqlQuery;
import com.dr.framework.core.security.entity.Role;
import com.dr.framework.core.security.service.SecurityManager;
import com.dr.framework.sys.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;

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
     * 查询指定角色所有的权限
     *
     * @param id
     * @return
     */
    @PostMapping("rolePermission")
    public ResultListEntity<String> rolePermission(String id) {
        return ResultListEntity.success(securityManager.rolePermissions(id).stream().map(BaseEntity::getId).collect(Collectors.toList()));
    }

    /**
     * 查询指定角色所有的用户
     *
     * @param id
     * @return
     */
    @PostMapping("roleUser")
    public ResultListEntity<String> roleUser(String id) {
        return ResultListEntity.success(securityManager.roleUsers(id).stream().map(BaseEntity::getId).collect(Collectors.toList()));
    }

    /**
     * 绑定关联用户角色
     *
     * @param id
     * @param personIds
     * @return
     */
    @PostMapping("bindRoleUser")
    public ResultEntity<String> bindRoleUser(String id, String personIds) {
        securityManager.bindRoleUsers(id, personIds);
        return ResultEntity.success("绑定成功!");
    }

    @Override
    protected SqlQuery<Role> buildPageQuery(HttpServletRequest request, Role entity) {
        SqlQuery<Role> sqlQuery = SqlQuery.from(service.getEntityRelation());
        return sqlQuery;
    }

    @Override
    @RequestMapping("/insert")
    public ResultEntity<Role> insert(HttpServletRequest request, Role entity) {
        ResultEntity<Role> result = super.insert(request, entity);
        String permissions = request.getParameter("permissions");
        //绑定权限角色
        securityManager.bindRolePermissions(entity.getId(), permissions);
        return result;
    }

    @Override
    @RequestMapping("/update")
    public ResultEntity<Role> update(HttpServletRequest request, Role entity) {
        ResultEntity<Role> result = super.update(request, entity);
        String permissions = request.getParameter("permissions");
        //绑定权限角色
        securityManager.bindRolePermissions(entity.getId(), permissions);
        return result;
    }
}
