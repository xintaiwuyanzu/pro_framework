package com.dr.framework.core.orm.support.mybatis.spring;

import com.dr.framework.core.orm.support.mybatis.spring.boot.autoconfigure.MultiDataSourceProperties;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.*;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.boot.context.properties.source.ConfigurationPropertyNameAliases;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;
import org.springframework.boot.jdbc.DatabaseDriver;
import org.springframework.boot.jdbc.XADataSourceWrapper;
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import javax.sql.XADataSource;

/**
 * 用来动态创建datasource，有可能配置多个数据源
 *
 * @author dr
 */
public class DataSourceFactory implements FactoryBean<DataSource>, BeanClassLoaderAware, BeanFactoryAware, InitializingBean, DisposableBean {
    private MultiDataSourceProperties properties;
    private ClassLoader classLoader;
    private BeanFactory beanFactory;
    private String beanName;
    private DataSource dataSource;
    Logger logger = LoggerFactory.getLogger(DataSourceFactory.class);

    @Override
    public void afterPropertiesSet() throws Exception {
        if (properties.isUseXa()) {
            try {
                XADataSource xaDataSource = createXaDataSource();
                XADataSourceWrapper wrapper = beanFactory.getBean(XADataSourceWrapper.class);
                dataSource = wrapper.wrapDataSource(xaDataSource);
                if (dataSource instanceof AtomikosDataSourceBean) {
                    ((AtomikosDataSourceBean) dataSource).setMaxPoolSize(50);
                }
            } catch (Exception e) {
                logger.error("创建多数据源失败，请检查添加spring-jta相关的依赖或数据库驱动版本", e);
            }
        } else {
            dataSource = properties.initializeDataSourceBuilder().build();
        }
        if (dataSource instanceof BeanNameAware) {
            ((BeanNameAware) dataSource).setBeanName(beanName);
        }
        if (dataSource instanceof HikariDataSource) {
            ((HikariDataSource) dataSource).setPoolName("HikariPool-" + properties.getName() + "-");
        }
        if (dataSource instanceof InitializingBean) {
            ((InitializingBean) dataSource).afterPropertiesSet();
        }
    }

    @Override
    public void destroy() throws Exception {
        if (dataSource instanceof DisposableBean) {
            ((DisposableBean) dataSource).destroy();
        }
        properties.close();
    }

    @Override
    public Class<?> getObjectType() {
        return DataSource.class;
    }

    @Override
    public DataSource getObject() {
        return dataSource;
    }

    private XADataSource createXaDataSource() {
        String className = this.properties.getXa().getDataSourceClassName();
        if (!StringUtils.hasLength(className)) {
            className = DatabaseDriver.fromJdbcUrl(this.properties.determineUrl()).getXaDataSourceClassName();
        }
        Assert.state(StringUtils.hasLength(className), "No XA DataSource class name specified");
        XADataSource xaDataSourceInstance = createXaDataSourceInstance(className);
        bindXaProperties(xaDataSourceInstance, this.properties);
        return xaDataSourceInstance;
    }

    private XADataSource createXaDataSourceInstance(String className) {
        try {
            Class<?> dataSourceClass = ClassUtils.forName(className, this.classLoader);
            Object instance = BeanUtils.instantiateClass(dataSourceClass);
            Assert.isInstanceOf(XADataSource.class, instance);
            return (XADataSource) instance;
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to create XADataSource instance from '" + className + "'");
        }
    }

    private void bindXaProperties(XADataSource target, DataSourceProperties dataSourceProperties) {
        Binder binder = new Binder(getBinderSource(dataSourceProperties));
        binder.bind(ConfigurationPropertyName.EMPTY, Bindable.ofInstance(target));
    }

    private ConfigurationPropertySource getBinderSource(DataSourceProperties dataSourceProperties) {
        MapConfigurationPropertySource source = new MapConfigurationPropertySource();
        source.put("user", this.properties.determineUsername());
        source.put("password", this.properties.determinePassword());
        source.put("url", this.properties.determineUrl());
        source.putAll(dataSourceProperties.getXa().getProperties());
        ConfigurationPropertyNameAliases aliases = new ConfigurationPropertyNameAliases();
        aliases.addAliases("user", "username");
        return source.withAliases(aliases);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public MultiDataSourceProperties getProperties() {
        return properties;
    }

    public void setProperties(MultiDataSourceProperties properties) {
        this.properties = properties;
    }

    public void setBeanName(String name) {
        this.beanName = name;
    }

    public String getBeanName() {
        return beanName;
    }
}
