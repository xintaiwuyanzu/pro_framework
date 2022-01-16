package com.dr.framework.core.web.interceptor;

import com.dr.framework.core.organise.entity.Organise;
import com.dr.framework.core.organise.entity.Person;
import com.dr.framework.core.organise.service.LoginService;
import com.dr.framework.core.organise.service.OrganisePersonService;
import com.dr.framework.core.security.SecurityHolder;
import com.dr.framework.core.security.bo.ClientInfo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.dr.framework.core.security.SecurityHolder.*;

/**
 * 登录人员拦截器
 * TODO 这里需要优化一下，可以结合组织机构从缓存中取数据的
 *
 * @author dr
 */
@Component
public class PersonInterceptor implements HandlerInterceptor, InitializingBean {
    public static final int ORDER = 1;
    @Autowired
    @Lazy
    LoginService loginService;
    @Autowired
    @Lazy
    OrganisePersonService organisePersonService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        //处理登录人员信息
        handlePersonInfo(request);
        //处理机构信息
        Organise organise = handleOrganiseInfo(request);
        //处理客户端信息
        ClientInfo clientInfo = handleClientInfo(request);
        //设置当前线程变量
        SecurityHolder.set(new DefaultSecurityHolder(clientInfo, organise));
        return true;
    }

    /**
     * 处理当前登陆人信息
     *
     * @param request
     */
    protected void handlePersonInfo(HttpServletRequest request) {
        if (request.getAttribute(CURRENT_PERSON_KEY) == null) {
            String token = getToken(request);
            if (!StringUtils.isEmpty(token)) {
                Person person = loginService.deAuth(token);
                request.setAttribute(CURRENT_PERSON_KEY, person);
            } else {
                request.removeAttribute(CURRENT_PERSON_KEY);
            }
        }
    }


    /**
     * 处理当前登录人信息
     *
     * @param request
     * @return
     */
    protected Organise handleOrganiseInfo(HttpServletRequest request) {
        Organise organise = (Organise) request.getAttribute(CURRENT_ORGANISE_KEY);
        if (organise == null) {
            Person person = (Person) request.getAttribute(CURRENT_PERSON_KEY);
            if (person != null) {
                organise = organisePersonService.getPersonDefaultOrganise(person.getId());
                request.setAttribute(CURRENT_ORGANISE_KEY, organise);
            }
        }
        return organise;
    }

    /**
     * 处理访问客户端信息
     *
     * @param request
     * @return
     */
    protected ClientInfo handleClientInfo(HttpServletRequest request) {
        ClientInfo clientInfo = (ClientInfo) request.getAttribute(ClientInfo.CLIENT_INFO_KEY);
        if (clientInfo == null) {
            Person person = (Person) request.getAttribute(CURRENT_PERSON_KEY);
            clientInfo = new ClientInfo();
            clientInfo.setPerson(person);
            clientInfo.setUserToken(getToken(request));
            clientInfo.setRemoteIp(getIPAddress(request));
            request.setAttribute(ClientInfo.CLIENT_INFO_KEY, clientInfo);
        }
        return clientInfo;
    }

    protected String getToken(HttpServletRequest request) {
        String token = doGetToken(request, TOKEN_HEADER_KEY);
        if (StringUtils.isEmpty(token)) {
            token = doGetToken(request, "dauth");
        }
        return token;
    }

    protected String doGetToken(HttpServletRequest request, String tokenHeaderKey) {
        String token = request.getParameter(tokenHeaderKey);
        if (StringUtils.isEmpty(token)) {
            token = (String) request.getAttribute(tokenHeaderKey);
            if (StringUtils.isEmpty(token)) {
                token = request.getHeader(tokenHeaderKey);
                if (StringUtils.isEmpty(token)) {
                    Cookie[] cookies = request.getCookies();
                    if (cookies != null) {
                        for (Cookie cookie : cookies) {
                            if (cookie.getName().equals(tokenHeaderKey)) {
                                token = cookie.getValue();
                                break;
                            }
                        }
                    }
                }
            }
        }
        if (!StringUtils.isEmpty(token)) {
            request.setAttribute(tokenHeaderKey, token);
        }
        return token;
    }

    protected String getIPAddress(HttpServletRequest request) {
        String ip = null;
        //X-Forwarded-For：Squid 服务代理
        String ipAddresses = request.getHeader("X-Forwarded-For");
        if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            //Proxy-Client-IP：apache 服务代理
            ipAddresses = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            //WL-Proxy-Client-IP：weblogic 服务代理
            ipAddresses = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            //HTTP_CLIENT_IP：有些代理服务器
            ipAddresses = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            //X-Real-IP：nginx服务代理
            ipAddresses = request.getHeader("X-Real-IP");
        }
        //有些网络通过多层代理，那么获取到的ip就会有多个，一般都是通过逗号（,）分割开来，并且第一个ip为客户端的真实IP
        if (ipAddresses != null && ipAddresses.length() != 0) {
            ip = ipAddresses.split(",")[0];
        }
        //还是不能获取到，最后再通过request.getRemoteAddr();获取
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.startsWith("0:0:0:0:0:0:0:1")) {
            //TODO 暴力处理ip6的问题
            ip = ip.replaceFirst("0:0:0:0:0:0:0:1", "localhost");
        }
        return ip;
    }

    @Override
    public void afterPropertiesSet() {
        //TODO 再议，主线程设置登陆人员为管理员
        SecurityHolder.set(new DefaultSecurityHolder(new ClientInfo(), null));
    }
}
