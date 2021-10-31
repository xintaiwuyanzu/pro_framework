package com.dr.framework.sys.controller;

import com.dr.framework.autoconfig.CommonConfig;
import com.dr.framework.common.controller.BaseController;
import com.dr.framework.common.entity.ResultEntity;
import com.dr.framework.core.organise.entity.Person;
import com.dr.framework.core.organise.service.LoginService;
import com.dr.framework.core.organise.service.PassWordEncrypt;
import com.dr.framework.core.security.SecurityHolder;
import com.dr.framework.core.security.bo.ClientInfo;
import com.dr.framework.core.web.annotations.Current;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;

/**
 * 用户登录相关api，
 *
 * @author dr
 * @author lc
 */
@RestController
@RequestMapping("${common.api-path:/api}/login")
public class LoginController {

    @Autowired
    LoginService loginService;
    @Autowired
    CommonConfig commonConfig;
    /**
     * 默认登录超时时间为30分钟
     */
    @Value("${server.session.timeout:30m}")
    Duration timeout;
    @Autowired
    @Lazy
    PassWordEncrypt passWordEncrypt;

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
            , HttpServletRequest request
            , HttpServletResponse response) {
        try {
            Person person = loginService.login(username, password, loginType, clientInfo.getRemoteIp());
            String token = loginService.auth(person);
            Cookie cookie = new Cookie(SecurityHolder.TOKEN_HEADER_KEY, token);
            response.addHeader(SecurityHolder.TOKEN_HEADER_KEY, token);
            //设置超时时间为2小时
            String path = request.getContextPath();
            if (StringUtils.isEmpty(path)) {
                path = "/";
            }
            cookie.setMaxAge((int) timeout.getSeconds());
            cookie.setPath(path);
            cookie.setHttpOnly(true);
            if (!StringUtils.isEmpty(clientInfo.getRemoteIp())) {
                //异常排除
                cookie.setDomain(clientInfo.getRemoteIp());
            }
            response.addCookie(cookie);
            return ResultEntity.success(token);
        } catch (Exception e) {
            return ResultEntity.error("用户名或密码错误");
        }
    }


    /**
     * 获取当前登陆人详细信息
     *
     * @param request
     * @return
     */
    @RequestMapping("/info")
    public ResultEntity<Person> personInfo(HttpServletRequest request) {
        Person person = BaseController.getUserLogin(request);
        if (person != null) {
            return ResultEntity.success(person);
        } else {
            return ResultEntity.error("用户未登录！");
        }
    }

    /**
     * 重置密码
     *
     * @param personId
     * @return
     */
    @PostMapping("/resetPassword")
    public ResultEntity<String> resetPassword(String personId) {
        //TODO 操作用户权限
        try {
            loginService.changePassword(personId, passWordEncrypt.encodePassword(commonConfig.getDefaultPassWord()));
            return ResultEntity.success("重置成功，新密码为【" + commonConfig.getDefaultPassWord() + "】");
        } catch (Exception e) {
            return ResultEntity.error(e.getMessage());
        }
    }


}
