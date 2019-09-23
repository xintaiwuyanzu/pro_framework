package com.dr.framework.core.web.resolver;

import com.dr.framework.common.entity.ResultEntity;
import com.dr.framework.core.security.bo.ClientInfo;
import com.dr.framework.core.web.annotations.Current;
import com.dr.framework.sys.controller.LoginController;
import com.dr.framework.core.organise.entity.Organise;
import com.dr.framework.core.organise.entity.Person;
import com.dr.framework.core.organise.service.SysOrganisePersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

/**
 * @author dr
 */
@Component
public class CurrentParamResolver implements HandlerMethodArgumentResolver {
    List<Class> supportClass = Arrays.asList(Person.class, Organise.class, ClientInfo.class);
    @Autowired
    LoginController loginController;
    @Autowired
    SysOrganisePersonService sysOrganisePersonService;
    static final String CLIENT_INFO_KEY = "__client_info";

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Class type = parameter.getParameterType();
        Current current = parameter.getParameterAnnotation(Current.class);
        ResultEntity<Person> resultEntity = loginController.personInfo(webRequest.getNativeRequest(HttpServletRequest.class));
        if (current.required() && !type.equals(ClientInfo.class)) {
            Assert.isTrue(resultEntity.isSuccess(), resultEntity.getMessage());
        }
        Person person = resultEntity.getData();
        if (type.equals(Person.class)) {
            return person;
        } else if (type.equals(Organise.class)) {
            if (person != null) {
                return sysOrganisePersonService.getPersonDefaultOrganise(person.getId());
            }
        } else if (type.equals(ClientInfo.class)) {
            ClientInfo clientInfo = (ClientInfo) webRequest.getAttribute(CLIENT_INFO_KEY, RequestAttributes.SCOPE_REQUEST);
            if (clientInfo == null) {
                String personId = person == null ? null : person.getId();
                clientInfo = new ClientInfo(personId);
                clientInfo.setRemoteIp(getIPAddress(webRequest));
            }
            return clientInfo;
        }
        return null;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(Current.class) && supportClass.contains(parameter.getParameterType());
    }

    String getIPAddress(NativeWebRequest request) {
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
            ip = request.getNativeRequest(HttpServletRequest.class).getRemoteAddr();
        }
        return ip;
    }
}
