package com.dr.framework.autoconfig;

import com.dr.framework.core.web.resolver.CurrentParamResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;


/**
 * 注入当前参数拦截器
 *
 * @author dr
 */
@Configuration
@EnableConfigurationProperties(CommonConfig.class)
public class ApplicationAutoConfiguration extends WebMvcConfigurationSupport {
    @Autowired
    CurrentParamResolver currentParamResolver;

    @Override
    protected void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        super.addArgumentResolvers(argumentResolvers);
        argumentResolvers.add(currentParamResolver);

    }
}
