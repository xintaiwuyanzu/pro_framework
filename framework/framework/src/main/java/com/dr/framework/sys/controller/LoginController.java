package com.dr.framework.sys.controller;

import com.dr.framework.common.entity.ResultEntity;
import com.dr.framework.sys.entity.Person;
import com.dr.framework.sys.entity.SubSystem;
import com.dr.framework.sys.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author dr
 */
@RestController
@RequestMapping("/api/login")
public class LoginController {
    public static final String TOKEN_HEADER_KEY = "$token";
    public static final String CURRENT_PERSON_KEY = "$currentPerson";

    @Autowired
    LoginService loginService;

    @RequestMapping("/validate")
    public ResultEntity<String> validate(@RequestParam String username
            , @RequestParam String password
            , @RequestParam(defaultValue = LoginService.LOGIN_TYPE_DEFAULT) String loginType
            , @RequestParam(defaultValue = SubSystem.DEFAULT_SYSTEM_ID) String sysId
            , HttpServletRequest request
            , HttpServletResponse response) {
        String remoteIp = getIpAddr(request);
        String token = loginService.auth(username, password, loginType, sysId, remoteIp);
        response.addHeader(TOKEN_HEADER_KEY, token);
        response.addCookie(new Cookie(TOKEN_HEADER_KEY, token));
        return ResultEntity.success(token);
    }

    @RequestMapping("/info")
    public ResultEntity<Person> personInfo(HttpServletRequest request) {
        Object person = request.getAttribute(CURRENT_PERSON_KEY);
        if (person != null) {
            return ResultEntity.success(person);
        }
        String token = request.getParameter(TOKEN_HEADER_KEY);
        if (StringUtils.isEmpty(token)) {
            token = (String) request.getAttribute(TOKEN_HEADER_KEY);
            if (StringUtils.isEmpty(token)) {
                token = request.getHeader(TOKEN_HEADER_KEY);
                if (StringUtils.isEmpty(token)) {
                    Cookie[] cookies = request.getCookies();
                    if (cookies != null) {
                        for (Cookie cookie : cookies) {
                            if (cookie.getName().equals(TOKEN_HEADER_KEY)) {
                                token = cookie.getValue();
                                break;
                            }
                        }
                    }
                }
            }
        }
        if (StringUtils.isEmpty(token)) {
            return ResultEntity.error("用户未登录！");
        } else {
            Person person1 = loginService.deAuth(token);
            if (person1 != null) {
                request.setAttribute(CURRENT_PERSON_KEY, person1);
            } else {
                request.removeAttribute(CURRENT_PERSON_KEY);
            }
            return ResultEntity.success(person1);
        }
    }


    public String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x - forwarded - for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy - Client - IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL - Proxy - Client - IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
