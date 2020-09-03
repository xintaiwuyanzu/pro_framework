package com.dr.framework.core.orm.support.mybatis.spring.mapper;

import com.dr.framework.core.orm.support.mybatis.spring.MybatisConfigurationBean;
import org.apache.ibatis.binding.MapperMethod;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 单数据源的mapperproxy
 *
 * @author dr
 */
public class SingleMapperProxy extends AbstractMapperProxy {
    private transient MybatisConfigurationBean configurationBean;
    private final transient Map<Method, MapperMethod> methodCache;

    public SingleMapperProxy(MybatisConfigurationBean configurationBean, Class<?> mapperInterface) {
        super(mapperInterface);
        this.configurationBean = configurationBean;
        methodCache = Collections.synchronizedMap(new HashMap<>(mapperInterface.getDeclaredMethods().length));
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
