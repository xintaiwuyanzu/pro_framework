package com.dr.framework.common.cache;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cache.CacheManager;

/**
 * 拦截CacheManager 添加自定义状态实现
 *
 * @author dr
 */
public class CacheManagerBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof CacheManager && !(bean instanceof CacheManagerWrapper)) {
            return new CacheManagerWrapper((CacheManager) bean);
        }
        return bean;
    }
}
