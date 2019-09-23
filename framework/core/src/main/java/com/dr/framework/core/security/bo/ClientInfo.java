package com.dr.framework.core.security.bo;

/**
 * 辅助实体类，保存浏览器相关的信息
 *
 * @author dr
 */
public class ClientInfo {
    private String userId;
    private String remoteIp;
    //TODO  deviceinfo, browerinfo

    public ClientInfo(String userId) {
        this.userId = userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public String getRemoteIp() {
        return remoteIp;
    }

    public void setRemoteIp(String remoteIp) {
        this.remoteIp = remoteIp;
    }
}
