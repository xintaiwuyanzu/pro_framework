package com.dr.framework.core.orm.support.mybatis.spring.mapper;

import com.dr.framework.core.orm.support.mybatis.spring.MybatisConfigurationBean;
import com.dr.framework.core.orm.support.mybatis.spring.mapper.method.MapperMethod;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 单数据源的mapperproxy
 */
public class SingleMapperProxy extends AbstractMapperProxy {
    private transient MybatisConfigurationBean configurationBean;
    private final transient Map<Method, MapperMethod> methodCache;

    public SingleMapperProxy(MybatisConfigurationBean configurationBean, Class<?> mapperInterface) {
        super(mapperInterface);
        this.configurationBean = configurationBean;
        methodCache = new ConcurrentHashMap(mapperInterface.getDeclaredMethods().length);
    }

    @Override
    protected MapperMethod cachedMapperMethod(Method method, Class entityClass, MybatisConfigurationBean mybatisConfigurationBean) {
        return methodCache.computeIfAbsent(method, k -> new MapperMethod(mapperInterface, k, mybatisConfigurationBean));
    }

    @Override
    protected MybatisConfigurationBean findConfigBean(Method method, Class entityClass) {
        return configurationBean;
    }
}
