package com.dr.framework.autoconfig;

import com.dr.framework.common.cache.CacheManagerBeanPostProcessor;
import org.springframework.cache.annotation.CachingConfigurationSelector;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 自动配置缓存相关bean
 *
 * @author dr
 */
@Configuration
@EnableCaching
@Import(CachingConfigurationSelector.class)
public class CacheAutoConfiguration {
    @Bean
    public CacheManagerBeanPostProcessor cacheManagerBeanPostProcessor() {
        return new CacheManagerBeanPostProcessor();
    }
}
