package com.dr.framework.core.security.service;

import com.dr.framework.common.entity.TreeNode;
import com.dr.framework.common.page.Page;
import com.dr.framework.core.security.entity.Permission;
import com.dr.framework.core.security.entity.Role;
import com.dr.framework.core.security.entity.SysMenu;
import com.dr.framework.core.security.query.PermissionQuery;
import com.dr.framework.core.security.query.RoleQuery;
import com.dr.framework.core.security.entity.SubSystem;
import com.dr.framework.core.security.query.SubSysQuery;
import com.dr.framework.core.security.query.SysMenuQuery;

import java.util.Arrays;
import java.util.List;

/**
 * @author dr
 */
public interface SecurityManager {
    String SECURITY_MANAGER_CONTEXT_KEY = "SECURITY_MANAGER_CONTEXT";

    //三员权限三种类型
    /**
     * 系统管理员
     */
    String ROLE_TYPE_SYS_ADMIN = "SYS_ADMIN";
    /**
     * 安全管理员
     */
    String ROLE_TYPE_SECURITY_ADMIN = "SECURITY_ADMIN";
    /**
     * 审计管理员
     */
    String ROLE_TYPE_AUDIT_ADMIN = "AUDIT_ADMIN";

    /**
     * 获取当前已登陆的所有的客户端的信息
     *
     * @return
     */
    //TODO
    //List<ClientInfo> getAllClientInfos();

    /**
     * 根据用户id获取所有的登陆客户端信息
     *
     * @param userId
     * @return
     */
    //TODO
    //List<ClientInfo> getClientInfoByUserId(String userId);

    /**
     * 登出指定的客户端
     *
     * @param client
     */
    //void invalidate(ClientInfo client);

    /**
     * 登出该账号在其他浏览器的登陆信息
     *
     * @param client
     */
    /*default void invalidateOthers(ClientInfo client) {
        for (ClientInfo clientInfo : getClientInfoByUserId(client.getUserId())) {
            if (!client.equals(clientInfo)) {
                invalidate(clientInfo);
            }
        }
    }*/

    /**
     * 用户的角色列表
     *
     * @param userId
     * @return
     */
    List<Role> userRoles(String userId);

    /**
     * 用户权限列表
     *
     * @param userId
     * @return
     */
    List<Permission> userPermissions(String userId);

    /**
     * 是否有角色
     *
     * @param userid
     * @param roleCodes
     * @return
     */
    boolean hasRole(String userid, String... roleCodes);

    /**
     * 指定用户是否有指定的角色
     *
     * @param userid
     * @param roles
     * @return
     */
    default boolean hasRole(String userid, Role... roles) {
        return hasRole(userid, Arrays.stream(roles).map(role -> role.getCode()).toArray(String[]::new));
    }

    /**
     * 是否有权限
     *
     * @param userid
     * @param permissionCodes
     * @return
     */
    boolean hasPermission(String userid, String... permissionCodes);

    /**
     * 指定用户是否指定的权限
     *
     * @param userid
     * @param permissions
     * @return
     */
    default boolean hasPermission(String userid, Permission... permissions) {
        return hasPermission(userid, Arrays.stream(permissions).map(permission -> permission.getCode()).toArray(String[]::new));
    }

    /**
     * 给指定用户添加角色
     *
     * @param userId
     * @param roleIds
     * @return
     */
    boolean addRoleToUser(String userId, String... roleIds);

    /**
     * 删除用户的角色
     *
     * @param userId
     * @param roleIds
     * @return
     */
    long removeUserRole(String userId, String... roleIds);

    /**
     * 给指定用户添加权限
     *
     * @param userId
     * @param permissionIds
     * @return
     */
    boolean addPermissionToUser(String userId, String... permissionIds);

    /**
     * 删除用户的permission
     *
     * @param userId
     * @param permissionIds
     * @return
     */
    long removeUserPermission(String userId, String... permissionIds);

    /**
     * 给指定角色添加权限
     *
     * @param roleId
     * @param permissionIds
     * @return
     */
    boolean addPermissionToRole(String roleId, String... permissionIds);

    /**
     * 删除角色的权限
     *
     * @param roleId
     * @param permissionIds
     * @return
     */
    long removeRolePermission(String roleId, String... permissionIds);

    /**
     * 给指定用户添加菜单
     *
     * @param userId
     * @param menuIds
     * @return
     */
    boolean addMenuToUser(String userId, String... menuIds);

    /**
     * 删除用户菜单
     *
     * @param userId
     * @param menuIds
     * @return
     */
    long removeUserMenu(String userId, String... menuIds);

    /**
     * 给指定角色添加菜单
     *
     * @param roleId
     * @param menuIds
     * @return
     */
    boolean addMenuToRole(String roleId, String... menuIds);

    /**
     * 删除角色菜单
     *
     * @param roleId
     * @param menuIds
     * @return
     */
    long removeRoleMenu(String roleId, String... menuIds);

    /**
     * ====================================================================
     * 下面是权限本身增删改查相关的方法
     * =====================================================================
     */

    Role addRole(Role role);

    long updateRole(Role role);

    long deleteRole(String... roleCode);

    List<Role> selectRoleList(RoleQuery query);

    Page<Role> selectRolePage(RoleQuery query, int start, int end);

    Permission addPermission(Permission permission);

    long updatePermission(Permission permission);

    long deletePermission(String... permissionCode);

    List<Permission> selectPermissionList(PermissionQuery query);

    Page<Permission> selectPermissionPage(PermissionQuery query, int start, int end);

    /**
     * ================================================================
     * 菜单本身是权限的一种带有业务逻辑的实现
     * 也就是说菜单本身也就是权限，与菜单相类似的功能按钮、访问路径、文件、数据权限等等都可以理解为权限
     * <p>
     * 暂时将不同的权限放到不同的表中，通过连表查询的方式判断权限。
     * TODO 这种方法不太好实现分布式
     * 另外一种办法是将所有类型的权限数据都汇总到权限表中，不过这种办法会导致权限表数量暴增
     * ================================================================
     */

    SysMenu addMenu(SysMenu sysMenu);

    long updateMenu(SysMenu sysMenu);

    long deleteMenu(String... id);

    List<SysMenu> selectMenuList(SysMenuQuery query);

    Page<SysMenu> selectMenuPage(SysMenuQuery query, int start, int end);

    List<TreeNode> menuTree(String sysId, String personId, boolean all);

    /**
     * ================================================================
     * 子系统，这块暂时归到权限这里
     * ================================================================
     *
     * @return
     */

    SubSystem addSubSys(SubSystem subSystem);

    long updateSubSys(SubSystem subSystem);

    long deleteSubSys(String id);

    List<SubSystem> selectSubSysList(SubSysQuery query);

    Page<SubSystem> selectSubSysPage(SubSysQuery query, int start, int end);


}
