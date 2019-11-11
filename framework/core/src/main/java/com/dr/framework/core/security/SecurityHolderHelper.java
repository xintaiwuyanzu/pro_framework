package com.dr.framework.core.security;

import com.dr.framework.core.organise.entity.Organise;
import com.dr.framework.core.organise.entity.Person;
import com.dr.framework.core.security.bo.ClientInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NamedThreadLocal;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.annotation.Nullable;

/**
 * 工具类，辅助 {@link SecurityHolder}使用
 * <p>
 * java8 接口不能有私有实现，将来升级到java9的时候可以将此类的代码合并到SecurityHolder中
 *
 * @author dr
 */
final class SecurityHolderHelper {
    private static final ThreadLocal<SecurityHolder> securityHolders = new NamedThreadLocal<>("securityHolders");
    private static Logger logger = LoggerFactory.getLogger(SecurityHolder.class);

    static class EmptySecurityHolder implements SecurityHolder {
        @Override
        public ClientInfo getClientInfo() {
            return null;
        }

        @Override
        public Person currentPerson() {
            return null;
        }

        @Override
        public Organise currentOrganise() {
            return null;
        }

        @Override
        public String personToken() {
            return null;
        }
    }

    /**
     * 返回当前线程登录用户相关信息
     *
     * @return
     */
    @Nullable
    public static SecurityHolder get() {
        SecurityHolder securityHolder;
        try {
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            securityHolder = (SecurityHolder) requestAttributes.getAttribute(SecurityHolder.SECURITY_HOLDER_KEY, RequestAttributes.SCOPE_REQUEST);
            if (securityHolder == null) {
                securityHolder = securityHolders.get();
            }
            return securityHolder;
        } catch (Exception e) {
            securityHolder = securityHolders.get();
        }
        if (securityHolder == null) {
            logger.error("\n当前线程上下文环境没有获取到用户相关的信息，请检查！" +
                    "\n若在测试环境调用，请使用SecurityHolder.set()方法设置当前线程用户信息。" +
                    "\n若开启了多线程模式，请在开启多线程前使用SecurityHolder.set()方法设置对应线程用户信息。");
            securityHolder = new EmptySecurityHolder();
        }
        return securityHolder;
    }

    public static void set(SecurityHolder securityHolder) {
        try {
            if (securityHolder != null) {
                RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
                requestAttributes.setAttribute(ClientInfo.CLIENT_INFO_KEY, securityHolder.getClientInfo(), RequestAttributes.SCOPE_REQUEST);
                requestAttributes.setAttribute(SecurityHolder.SECURITY_HOLDER_KEY, securityHolder, RequestAttributes.SCOPE_REQUEST);
                requestAttributes.setAttribute(SecurityHolder.CURRENT_PERSON_KEY, securityHolder.currentPerson(), RequestAttributes.SCOPE_REQUEST);
                requestAttributes.setAttribute(SecurityHolder.TOKEN_HEADER_KEY, securityHolder.personToken(), RequestAttributes.SCOPE_REQUEST);
                requestAttributes.setAttribute(SecurityHolder.CURRENT_ORGANISE_KEY, securityHolder.currentOrganise(), RequestAttributes.SCOPE_REQUEST);
            }
        } catch (Exception e) {
            securityHolders.set(securityHolder);
        }
    }

    public static void reset() {
        try {
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            requestAttributes.removeAttribute(ClientInfo.CLIENT_INFO_KEY, RequestAttributes.SCOPE_REQUEST);
            requestAttributes.removeAttribute(SecurityHolder.SECURITY_HOLDER_KEY, RequestAttributes.SCOPE_REQUEST);
            requestAttributes.removeAttribute(SecurityHolder.CURRENT_PERSON_KEY, RequestAttributes.SCOPE_REQUEST);
            requestAttributes.removeAttribute(SecurityHolder.TOKEN_HEADER_KEY, RequestAttributes.SCOPE_REQUEST);
            requestAttributes.removeAttribute(SecurityHolder.CURRENT_ORGANISE_KEY, RequestAttributes.SCOPE_REQUEST);
            securityHolders.remove();
        } catch (Exception e) {
            securityHolders.remove();
        }
    }
}
