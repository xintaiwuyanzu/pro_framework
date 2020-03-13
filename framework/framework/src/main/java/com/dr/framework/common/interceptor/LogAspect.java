package com.dr.framework.common.interceptor;

import com.dr.framework.common.dao.CommonMapper;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author dr
 */
@Component
@Aspect
public class LogAspect {
    @Autowired
    CommonMapper commonMapper;
    Logger logger = LoggerFactory.getLogger(LogAspect.class);

    @Before("@annotation(com.dr.framework.common.annotations.Log)")
    public void test(JoinPoint joinPoint) {
        logger.error("aaa");
    }
}
