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


    @Nullable
    public static Person getPerson(RequestAttributes requestAttributes) {
        SecurityHolder securityHolder = get(requestAttributes);
        return securityHolder == null ? null : securityHolder.currentPerson();
    }

    @Nullable
    public static Organise getOrganise(RequestAttributes requestAttributes) {
        SecurityHolder securityHolder = get(requestAttributes);
        return securityHolder == null ? null : securityHolder.currentOrganise();
    }

    @Nullable
    public static ClientInfo getClientInfo(RequestAttributes requestAttributes) {
        SecurityHolder securityHolder = get(requestAttributes);
        return securityHolder == null ? null : securityHolder.getClientInfo();
    }

    /**
     * 获取上下文
     *
     * @param requestAttributes
     * @return
     */
    @Nullable
    public static SecurityHolder get(RequestAttributes requestAttributes) {
        return requestAttributes == null ? null : (SecurityHolder) requestAttributes.getAttribute(SecurityHolder.SECURITY_HOLDER_KEY, RequestAttributes.SCOPE_REQUEST);
    }

    /**
     * 设置上下文
     *
     * @param securityHolder
     * @param requestAttributes
     */
    public static void set(SecurityHolder securityHolder, RequestAttributes requestAttributes) {
        if (requestAttributes != null && securityHolder != null) {
            requestAttributes.setAttribute(ClientInfo.CLIENT_INFO_KEY, securityHolder.getClientInfo(), RequestAttributes.SCOPE_REQUEST);
            requestAttributes.setAttribute(SecurityHolder.SECURITY_HOLDER_KEY, securityHolder, RequestAttributes.SCOPE_REQUEST);
            requestAttributes.setAttribute(SecurityHolder.CURRENT_PERSON_KEY, securityHolder.currentPerson(), RequestAttributes.SCOPE_REQUEST);
            requestAttributes.setAttribute(SecurityHolder.TOKEN_HEADER_KEY, securityHolder.personToken(), RequestAttributes.SCOPE_REQUEST);
            requestAttributes.setAttribute(SecurityHolder.CURRENT_ORGANISE_KEY, securityHolder.currentOrganise(), RequestAttributes.SCOPE_REQUEST);
        }
    }

    /**
     * 清空上下文
     *
     * @param requestAttributes
     */
    public static void reset(RequestAttributes requestAttributes) {
        if (requestAttributes != null) {
            requestAttributes.removeAttribute(ClientInfo.CLIENT_INFO_KEY, RequestAttributes.SCOPE_REQUEST);
            requestAttributes.removeAttribute(SecurityHolder.SECURITY_HOLDER_KEY, RequestAttributes.SCOPE_REQUEST);
            requestAttributes.removeAttribute(SecurityHolder.CURRENT_PERSON_KEY, RequestAttributes.SCOPE_REQUEST);
            requestAttributes.removeAttribute(SecurityHolder.TOKEN_HEADER_KEY, RequestAttributes.SCOPE_REQUEST);
            requestAttributes.removeAttribute(SecurityHolder.CURRENT_ORGANISE_KEY, RequestAttributes.SCOPE_REQUEST);
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
            securityHolder = get(RequestContextHolder.getRequestAttributes());
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
        set(securityHolder, RequestContextHolder.getRequestAttributes());
        securityHolders.set(securityHolder);
    }

    public static void reset() {
        reset(RequestContextHolder.getRequestAttributes());
        securityHolders.remove();
    }
}
