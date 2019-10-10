package com.dr.framework.sys.service;


import com.dr.framework.common.service.DefaultDataBaseService;
import com.dr.framework.core.security.service.SecurityManager;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 默认权限相关的service
 *
 * @author dr
 */
@Service
public class DefaulrSecurityManager implements SecurityManager, InitializingBean {
    @Autowired
    DefaultDataBaseService defaultDataBaseService;

    @Override
    public boolean hasRole(String userid, String... roles) {
        return false;
    }

    @Override
    public boolean hasPermission(String userid, String... permissions) {
        return false;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
