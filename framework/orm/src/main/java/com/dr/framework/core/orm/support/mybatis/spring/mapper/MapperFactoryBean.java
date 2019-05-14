package com.dr.framework.core.orm.support.mybatis.spring.mapper;

import com.dr.framework.core.orm.support.mybatis.spring.MybatisConfigurationBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.*;
import org.springframework.util.Assert;

import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * 动态代理生成mapper接口实现bean
 *
 * @param <T>
 * @author dr
 */
public class MapperFactoryBean<T> implements FactoryBean<T>, BeanFactoryAware, BeanClassLoaderAware, InitializingBean {
    Logger logger = LoggerFactory.getLogger(MapperFactoryBean.class);

    private Class<T> mapperInterface;
    private ListableBeanFactory beanFactory;
    private ClassLoader classLoader;
    private T proxy;

    public MapperFactoryBean(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    @Override
    public T getObject() {
        return proxy;
    }

    @Override
    public void afterPropertiesSet() {
        Assert.notNull(mapperInterface, "mapperInterface 不能为空");
        Assert.notNull(classLoader, "classLoader 不能为空");
        Assert.notNull(beanFactory, "beanFactory 不能为空");
        try {
            Map<String, MybatisConfigurationBean> mybatisConfigurationBeanMap = beanFactory.getBeansOfType(MybatisConfigurationBean.class);
            AbstractMapperProxy mapperProxy = null;
            if (mybatisConfigurationBeanMap.size() == 1) {
                mapperProxy = new SingleMapperProxy(mybatisConfigurationBeanMap.values().iterator().next(), mapperInterface);
            } else if (mybatisConfigurationBeanMap.size() > 1) {
                MybatisConfigurationBean primaryBean;
                try {
                    primaryBean = beanFactory.getBean(MybatisConfigurationBean.class);
                } catch (Exception e) {
                    primaryBean = mybatisConfigurationBeanMap.values().iterator().next();
                    logger.debug("没找到默认数据源，使用第一个数据源【{}】作为默认数据源", primaryBean.getDatabaseId());
                }
                mapperProxy = new MultiMapperProxy(primaryBean, mybatisConfigurationBeanMap, mapperInterface);
            }
            proxy = (T) Proxy.newProxyInstance(classLoader, new Class[]{mapperInterface}, mapperProxy);
            Assert.notNull(proxy, "创建" + mapperInterface + "代理失败");
        } catch (Exception e) {
            logger.error("创建" + mapperInterface.getName() + "代理失败", e);
        }
    }

    @Override
    public Class<?> getObjectType() {
        return mapperInterface;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = (ListableBeanFactory) beanFactory;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public void setMapperInterface(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

}
