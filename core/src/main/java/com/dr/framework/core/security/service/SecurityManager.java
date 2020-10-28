package com.dr.framework.core.security.service;

import com.dr.framework.core.organise.entity.Person;
import com.dr.framework.core.security.bo.PermissionMatcher;
import com.dr.framework.core.security.entity.Permission;
import com.dr.framework.core.security.entity.Role;
import com.dr.framework.core.util.Constants;

import java.util.List;

/**
 * @author dr
 */
public interface SecurityManager {
    String SECURITY_MANAGER_CONTEXT_KEY = "SECURITY_MANAGER_CONTEXT";
    String defaultMatcher = "*";
    /**
     * 权限字符串分隔符
     */
    String WILDCARD_PERMISSION_SEPARATOR = ":";

    String groupSplit = ";";
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
     * 角色的权限
     *
     * @param roleId 角色Id
     * @return
     */
    List<Permission> rolePermissions(String roleId);

    /**
     * 绑定权限到角色
     *
     * @param roleId
     * @param permissions
     * @return
     */
    long bindRolePermissions(String roleId, String permissions);

    /**
     * 绑定角色和指定的用户
     *
     * @param roleId
     * @param personIds
     * @return
     */
    long bindRoleUsers(String roleId, String personIds);

    /**
     * 用户的所有权限组
     *
     * @param userId
     * @param permissionType
     * @param permissionGroup
     * @return
     */
    List<PermissionMatcher> userPermissions(String userId, String permissionType, String permissionGroup);

    /**
     * 指定角色的所有用户
     *
     * @param roleId
     * @return
     */
    List<Person> roleUsers(String roleId);

    /**
     * 是否有角色
     *
     * @param userid
     * @param roleCodes
     * @return
     */
    boolean hasRole(String userid, String... roleCodes);

    /**
     * 给指定用户添加角色
     *
     * @param userId
     * @param roleIds
     * @return
     */
    long addRoleToUser(String userId, String... roleIds);

    /**
     * 删除用户的角色
     *
     * @param userId
     * @param roleIds
     * @return
     */
    long removeUserRole(String userId, String... roleIds);

    /**
     * 是否有指定的权限
     *
     * @param userid
     * @param permissionType
     * @param permissionCode
     * @return
     */
    default boolean hasPermission(String userid, String permissionType, String permissionCode) {
        return hasPermission(userid, permissionType, Constants.DEFAULT, permissionCode);
    }

    /**
     * 是否有权限
     *
     * @param userid
     * @param permissionType
     * @param permissionGroup
     * @param permissionCode
     * @return
     */
    boolean hasPermission(String userid, String permissionType, String permissionGroup, String permissionCode);

    /**
     * 给指定角色添加权限
     *
     * @param roleId
     * @param permissionIds
     * @return
     */
    long addPermissionToRole(String roleId, String... permissionIds);

    /**
     * 删除角色的权限
     *
     * @param roleId
     * @param permissionIds
     * @return
     */
    long removeRolePermission(String roleId, String... permissionIds);


}
