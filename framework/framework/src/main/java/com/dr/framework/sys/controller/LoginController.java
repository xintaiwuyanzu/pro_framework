package com.dr.framework.sys.controller;

import com.dr.framework.common.entity.ResultEntity;
import com.dr.framework.core.organise.entity.Person;
import com.dr.framework.core.organise.service.LoginService;
import com.dr.framework.core.security.SecurityHolder;
import com.dr.framework.core.security.bo.ClientInfo;
import com.dr.framework.core.web.annotations.Current;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用户登录相关api，
 *
 * @author dr
 */
@RestController
@RequestMapping("${common.api-path:/api}/login")
public class LoginController {
    @Autowired
    LoginService loginService;

    /**
     * TODO 这里需要处理登陆时加密密码传参的相关方法
     *
     */

    /**
     * 登录校验
     *
     * @param username   用户名
     * @param password   密码
     * @param loginType  登录类型
     * @param clientInfo 客户端信息
     * @param response
     * @return
     */
    @RequestMapping("/validate")
    public ResultEntity<String> validate(@RequestParam String username
            , @RequestParam String password
            , @RequestParam(defaultValue = LoginService.LOGIN_TYPE_DEFAULT) String loginType
            , @Current ClientInfo clientInfo
            , HttpServletResponse response) {
        String token = loginService.auth(username, password, loginType, clientInfo.getRemoteIp());
        response.addHeader(SecurityHolder.TOKEN_HEADER_KEY, token);
        response.addCookie(new Cookie(SecurityHolder.TOKEN_HEADER_KEY, token));
        return ResultEntity.success(token);
    }

    /**
     * 获取当前登陆人详细信息
     *
     * @param request
     * @return
     */
    @RequestMapping("/info")
    public ResultEntity<Person> personInfo(HttpServletRequest request) {
        Object person = request.getAttribute(SecurityHolder.CURRENT_PERSON_KEY);
        if (person != null) {
            return ResultEntity.success(person);
        } else {
            return ResultEntity.error("用户未登录！");
        }
    }
}
