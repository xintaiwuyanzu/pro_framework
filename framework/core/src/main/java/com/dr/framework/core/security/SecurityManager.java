package com.dr.framework.core.security;

import com.dr.framework.core.security.bo.Permission;
import com.dr.framework.core.security.bo.Role;

import java.util.Arrays;
import java.util.List;

public interface SecurityManager {
    final String SECURITY_MANAGER_CONTEXT_KEY = "SECURITY_MANAGER_CONTEXT";

    public default ClientInfo currentClientInfo() {
        return SecurityHolder.get();
    }

    /**
     * 登陆用户
     *
     * @return
     */
    public default boolean login(String userName, String password) {
        return login(userName, password, false);
    }

    /**
     * 登陆用户
     *
     * @param userName   用户名
     * @param password   密码
     * @param rememberMe 是否记住用户
     * @return
     */
    public boolean login(String userName, String password, boolean rememberMe);

    /**
     * 获取当前已登陆的所有的客户端的信息
     *
     * @return
     */
    public List<ClientInfo> getAllClientInfos();

    /**
     * 根据用户id获取所有的登陆客户端信息
     *
     * @param userId
     * @return
     */
    public List<ClientInfo> getClientInfoByUserId(String userId);

    /**
     * 登出指定的客户端
     *
     * @param client
     */
    public void invalidate(ClientInfo client);

    /**
     * 登出该账号在其他浏览器的登陆信息
     *
     * @param client
     */
    public default void invalidateOthers(ClientInfo client) {
        for (ClientInfo clientInfo : getClientInfoByUserId(client.getUserId())) {
            if (!client.equals(clientInfo)) {
                invalidate(clientInfo);
            }
        }
    }

    /**
     * 是否有角色
     *
     * @param userid
     * @param roles
     * @return
     */
    public boolean hasRole(String userid, String... roles);

    public default boolean hasRole(String userid, Role... roles) {
        return hasRole(userid, Arrays.stream(roles).map(role -> role.getCode()).toArray(String[]::new));
    }

    /**
     * 是否有权限
     *
     * @param userid
     * @param permissions
     * @return
     */
    public boolean hasPermission(String userid, String... permissions);

    public default boolean hasPermission(String userid, Permission... permissions) {
        return hasPermission(userid, Arrays.stream(permissions).map(permission -> permission.getCode()).toArray(String[]::new));
    }

}
