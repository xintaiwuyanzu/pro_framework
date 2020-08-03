package com.dr.framework.core.security;

import com.dr.framework.core.organise.entity.Organise;
import com.dr.framework.core.organise.entity.Person;
import com.dr.framework.core.security.bo.ClientInfo;
import org.springframework.web.context.request.RequestAttributes;

/**
 * ThreadLocal保存当前登陆人相关信息
 *
 * @author dr
 */
public interface SecurityHolder {
    String TOKEN_HEADER_KEY = "$token";
    String CURRENT_PERSON_KEY = "$currentPerson";
    String CURRENT_ORGANISE_KEY = "$currentORGANISE";
    String SECURITY_HOLDER_KEY = "$SecurityHolder";

    static SecurityHolder get(RequestAttributes requestAttributes) {
        return SecurityHolderHelper.get(requestAttributes);
    }

    /**
     * 获取当前线程默认的上下文信息
     *
     * @return
     */
    static SecurityHolder get() {
        return SecurityHolderHelper.get();
    }

    /**
     * 设置当前线程默认的用户上下文信息
     *
     * @param securityHolder
     */
    static void set(SecurityHolder securityHolder) {
        SecurityHolderHelper.set(securityHolder);
    }

    /**
     * 设置当前线程默认的用户上下文信息
     *
     * @param securityHolder
     */
    static void set(SecurityHolder securityHolder, RequestAttributes requestAttributes) {
        SecurityHolderHelper.set(securityHolder, requestAttributes);
    }

    /**
     * 重置清空当前线程默认的用户上下文信息
     */
    static void reset() {
        SecurityHolderHelper.reset();
    }

    static void reset(RequestAttributes requestAttributes) {
        SecurityHolderHelper.reset(requestAttributes);
    }

    /**
     * 获取客户端访问信息
     *
     * @return
     */
    ClientInfo getClientInfo();

    /**
     * 获取用户信息
     *
     * @return
     */
    Person currentPerson();

    /**
     * 获取机构信息
     *
     * @return
     */
    Organise currentOrganise();

    /**
     * 获取用户token信息
     *
     * @return
     */
    String personToken();
}
