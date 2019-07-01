package com.dr;

import com.dr.framework.core.orm.support.mybatis.spring.boot.autoconfigure.EnableAutoMapper;
import com.dr.framework.core.web.resolver.CurrentParamResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.session.SessionAutoConfiguration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

@SpringBootApplication(exclude = SessionAutoConfiguration.class)
@EnableAutoMapper
public class Application extends WebMvcConfigurationSupport {
    @Autowired
    CurrentParamResolver currentParamResolver;

    @Override
    protected void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        super.addArgumentResolvers(argumentResolvers);
        argumentResolvers.add(currentParamResolver);

    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
