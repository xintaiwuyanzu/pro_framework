package com.dr.framework.sys.service;

import com.dr.framework.autoconfig.CommonConfig;
import com.dr.framework.common.dao.CommonMapper;
import com.dr.framework.core.organise.entity.Person;
import com.dr.framework.core.security.SecurityHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpSession;

/**
 * 默认实现
 *
 * @author dr
 */
public class DefaultLoginTokenHandler implements LoginTokenHandler {
    @Autowired
    CommonConfig commonConfig;
    @Autowired
    CommonMapper commonMapper;

    @Override
    public String auth(Person person) {
        if (commonConfig.isSessionAuth()) {
            HttpSession session = getSession();
            if (session != null) {
                //TODO,别的线程改掉了登录用户信息
                session.setAttribute(SecurityHolder.CURRENT_PERSON_KEY, person);
                return session.getId();
            }
        }
        return person.getId();
    }

    @Override
    public Person deAuth(String token) {
        if (commonConfig.isSessionAuth()) {
            HttpSession session = getSession();
            if (session != null) {
                return (Person) session.getAttribute(SecurityHolder.CURRENT_PERSON_KEY);
            }
            return null;
        } else {
            Assert.isTrue(StringUtils.hasText(token), "token不能为空！");
            return commonMapper.selectById(Person.class, token);
        }
    }

    /**
     * 根据线程上下文获取session信息
     *
     * @return
     */
    HttpSession getSession() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            HttpSession session = (HttpSession) requestAttributes.resolveReference(RequestAttributes.REFERENCE_SESSION);
            return session;
        }
        return null;
    }

}
