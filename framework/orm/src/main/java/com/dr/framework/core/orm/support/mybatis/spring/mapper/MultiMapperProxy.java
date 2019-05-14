package com.dr.framework.core.orm.support.mybatis.spring.mapper;

import com.dr.framework.core.orm.annotations.Mapper;
import com.dr.framework.core.orm.support.mybatis.spring.MybatisConfigurationBean;
import com.dr.framework.core.orm.support.mybatis.spring.mapper.method.MapperMethod;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author dr
 */
public class MultiMapperProxy extends AbstractMapperProxy {
    private transient Map<String, MybatisConfigurationBean> mybatisConfigurationBeanMap;
    private final transient Map<MybatisConfigurationBean, Map<Method, MapperMethod>> methodCache;
    /**
     * 默认主要数据源
     */
    private transient MybatisConfigurationBean primaryBean;

    public MultiMapperProxy(MybatisConfigurationBean primaryBean, Map<String, MybatisConfigurationBean> mybatisConfigurationBeanMap, Class<?> mapperInterface) {
        super(mapperInterface);
        this.mybatisConfigurationBeanMap = mybatisConfigurationBeanMap;
        methodCache = new ConcurrentHashMap<>(mybatisConfigurationBeanMap.size());
        this.primaryBean = primaryBean;
    }

    @Override
    protected MapperMethod cachedMapperMethod(Method method, Class entityClass, MybatisConfigurationBean mybatisConfigurationBean) {
        Map<Method, MapperMethod> methodMapperMethodMap = methodCache.computeIfAbsent(mybatisConfigurationBean, k -> new ConcurrentHashMap<>(mapperInterface.getDeclaredMethods().length));
        return methodMapperMethodMap.computeIfAbsent(method, k -> new MapperMethod(mapperInterface, k, mybatisConfigurationBean));
    }

    @Override
    protected MybatisConfigurationBean findConfigBean(Method method, Class entityClass) {
        if (entityClass != null) {
            for (MybatisConfigurationBean mybatisConfigurationBean : mybatisConfigurationBeanMap.values()) {
                if (mybatisConfigurationBean.containsEntity(entityClass)) {
                    return mybatisConfigurationBean;
                }
            }
        } else {
            Class mapperInterface = method.getDeclaringClass();
            if (mapperInterface.isAnnotationPresent(Mapper.class)) {
                Mapper mapper = (Mapper) mapperInterface.getAnnotation(Mapper.class);
                for (String module : mapper.module()) {
                    for (MybatisConfigurationBean mybatisConfigurationBean : mybatisConfigurationBeanMap.values()) {
                        if (mybatisConfigurationBean.containModules(module)) {
                            return mybatisConfigurationBean;
                        }
                    }
                }
            }
        }
        return primaryBean;
    }
}
