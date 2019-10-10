package com.dr.framework.core.security.service;

import com.dr.framework.core.security.entity.Permission;
import com.dr.framework.core.security.entity.Role;

import java.util.Arrays;

/**
 * @author dr
 */
public interface SecurityManager {
    String SECURITY_MANAGER_CONTEXT_KEY = "SECURITY_MANAGER_CONTEXT";

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
     * 是否有角色
     *
     * @param userid
     * @param roles
     * @return
     */
    boolean hasRole(String userid, String... roles);

    default boolean hasRole(String userid, Role... roles) {
        return hasRole(userid, Arrays.stream(roles).map(role -> role.getCode()).toArray(String[]::new));
    }

    /**
     * 是否有权限
     *
     * @param userid
     * @param permissions
     * @return
     */
    boolean hasPermission(String userid, String... permissions);

    default boolean hasPermission(String userid, Permission... permissions) {
        return hasPermission(userid, Arrays.stream(permissions).map(permission -> permission.getCode()).toArray(String[]::new));
    }

}
