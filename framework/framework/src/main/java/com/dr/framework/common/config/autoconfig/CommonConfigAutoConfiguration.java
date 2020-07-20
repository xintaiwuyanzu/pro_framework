package com.dr.framework.common.config.autoconfig;

import com.dr.framework.common.config.service.CommonConfigBeanFactory;
import com.dr.framework.common.config.service.impl.DefaultCommonConfigBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 自动配置默认实现
 *
 * @author dr
 */
@Configuration
public class CommonConfigAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    CommonConfigBeanFactory configBeanFactory() {
        return new DefaultCommonConfigBeanFactory();
    }

}
