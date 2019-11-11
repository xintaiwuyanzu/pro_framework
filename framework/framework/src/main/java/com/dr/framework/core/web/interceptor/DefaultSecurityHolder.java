package com.dr.framework.core.web.interceptor;

import com.dr.framework.core.organise.entity.Organise;
import com.dr.framework.core.organise.entity.Person;
import com.dr.framework.core.security.SecurityHolder;
import com.dr.framework.core.security.bo.ClientInfo;

/**
 * 默认的SecurityHolder，给拦截器
 *
 * @author dr
 * @see PersonInterceptor 使用
 */
class DefaultSecurityHolder implements SecurityHolder {
    private ClientInfo clientInfo;
    private Organise organise;

    public DefaultSecurityHolder(ClientInfo clientInfo, Organise organise) {
        this.clientInfo = clientInfo;
        this.organise = organise;
    }

    @Override
    public ClientInfo getClientInfo() {
        return clientInfo;
    }

    @Override
    public Person currentPerson() {
        return clientInfo.getPerson();
    }

    @Override
    public Organise currentOrganise() {
        return organise;
    }

    @Override
    public String personToken() {
        return clientInfo.getUserToken();
    }
}
