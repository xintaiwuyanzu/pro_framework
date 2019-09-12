package com.dr.framework.core.security;

/**
 * 辅助实体类，保存浏览器相关的信息
 *
 * @author dr
 */
public class ClientInfo {
    private String userId;

    public ClientInfo(String userId) {

        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }
}
