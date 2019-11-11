package com.dr.framework.core.security.bo;

import com.dr.framework.core.organise.entity.Person;

/**
 * 辅助实体类，保存浏览器相关的信息
 *
 * @author dr
 */
public class ClientInfo {
    public static final String CLIENT_INFO_KEY = "__client_info";

    private Person person;
    private String userToken;
    private String remoteIp;
    //TODO  deviceinfo, browerinfo


    public String getRemoteIp() {
        return remoteIp;
    }

    public void setRemoteIp(String remoteIp) {
        this.remoteIp = remoteIp;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
}
