package com.dr.framework.sys.controller;

import com.dr.framework.common.entity.ResultEntity;
import com.dr.framework.common.service.CommonService;
import com.dr.framework.core.orm.sql.support.SqlQuery;
import com.dr.framework.sys.entity.UserLogin;
import com.dr.framework.sys.entity.UserLoginInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/login")
public class LoginController {
    @Autowired
    CommonService commonService;

    @RequestMapping("/validate")
    public ResultEntity validate(@RequestParam String username, @RequestParam String password, HttpServletRequest request) {
        SqlQuery<UserLogin> sqlQuery = SqlQuery.from(UserLogin.class).equal(UserLoginInfo.LOGINID, username);
        UserLogin userLogin = commonService.selectOne(sqlQuery);
        if (userLogin != null) {
            request.getSession(true).setAttribute("USERLOGIN", userLogin);
            return ResultEntity.success(userLogin);
        } else {
            return ResultEntity.error("未找到指定用户！");
        }
    }
}
