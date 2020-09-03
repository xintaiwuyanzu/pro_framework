package com.dr.framework.autoconfig;

import com.dr.framework.core.organise.service.PassWordEncrypt;
import com.dr.framework.core.web.interceptor.PersonInterceptor;
import com.dr.framework.core.web.resolver.CurrentParamResolver;
import com.dr.framework.sys.service.DefaultPassWordEncrypt;
import com.dr.framework.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;


/**
 * 注入当前参数拦截器
 *
 * @author dr
 */
@Configuration
@ConditionalOnWebApplication
@ComponentScan(Constants.PACKAGE_NAME)
@EnableConfigurationProperties(CommonConfig.class)
public class ApplicationAutoConfiguration extends WebMvcConfigurationSupport {
    @Autowired
    CurrentParamResolver currentParamResolver;
    @Autowired
    PersonInterceptor personInterceptor;

    /**
     * 如果项目没有加上密码加密的实现类，则使用默认的实现类
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(PassWordEncrypt.class)
    public PassWordEncrypt passWordEncrypt() {
        return new DefaultPassWordEncrypt("utf-8");
    }

    @Bean
    public WebMvcConfigurer webMvcConfigurer() {
        return new WebMvcConfigurer() {
            /**
             * 注入controller参数解析器
             *
             * @param argumentResolvers
             */
            @Override
            public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
                argumentResolvers.add(currentParamResolver);
            }

            /**
             * 注入当前登录人员拦截器
             *
             * @param registry
             */
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(personInterceptor).order(PersonInterceptor.ORDER);
            }
        };
    }

}
