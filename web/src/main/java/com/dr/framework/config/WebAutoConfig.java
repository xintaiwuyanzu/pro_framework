package com.dr.framework.config;

import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 添加上自定义的拦截器
 *
 * @author dr
 */
@Configuration
@ComponentScan("com.dr.framework.core.servlet")
@ServletComponentScan
public class WebAutoConfig {

}
