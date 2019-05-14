package com.dr.framework.core.orm.support.mybatis.spring.boot.autoconfigure;

import com.dr.framework.core.orm.support.mybatis.spring.DataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringUtils;

import static com.dr.framework.core.orm.support.mybatis.spring.boot.autoconfigure.MapperBeanDefinitionProcessor.PREFIX_KEY;

/**
 * 用来注入指定的数据库链接和事务管理器
 * 还有自定义规则的mapper接口
 *
 * @author dr
 */
public class MapperBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware, BeanClassLoaderAware {
    private ClassLoader classLoader;
    private Environment environment;
    private Logger logger = LoggerFactory.getLogger(MapperBeanDefinitionProcessor.class);

    @Override
    public void registerBeanDefinitions(AnnotationMetadata classMetadata, BeanDefinitionRegistry registry) {
        String autoMapperClassName = EnableAutoMapper.class.getName();
        if (classMetadata.hasAnnotation(autoMapperClassName)) {
            AnnotationAttributes annotationAttributes = AnnotationAttributes.fromMap(classMetadata.getAnnotationAttributes(autoMapperClassName));
            AnnotationAttributes[] databases = annotationAttributes.getAnnotationArray("databases");
            if (databases.length == 0) {
                MultiDataSourceProperties dataSourceProperties = readDataSourceProties(MapperBeanDefinitionProcessor.DEFAULT_PREFIX, null);
                BeanDefinition beanDefinition = buildBeanDefinition(dataSourceProperties, true);
                registerBeanDefintionIfNotExist(registry, beanDefinition, dataSourceProperties.getName());
            } else if (databases.length == 1) {
                AnnotationAttributes annotationAttributes1 = databases[0];
                MultiDataSourceProperties dataSourceProperties = readDataSourceProties(annotationAttributes1.getString("prefix"), annotationAttributes1.getString("name"));
                BeanDefinition beanDefinition = buildBeanDefinition(dataSourceProperties, true);
                registerBeanDefintionIfNotExist(registry, beanDefinition, dataSourceProperties.getName());
            } else {
                for (int i = 0; i < databases.length; i++) {
                    AnnotationAttributes database = databases[i];
                    boolean primary = database.getBoolean("primary");
                    MultiDataSourceProperties dataSourceProperties = readDataSourceProties(database.getString(PREFIX_KEY), database.getString("name"));
                    BeanDefinition beanDefinition = buildBeanDefinition(dataSourceProperties, primary);
                    registerBeanDefintionIfNotExist(registry, beanDefinition, dataSourceProperties.getName());
                }
            }

            BeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(MapperBeanDefinitionProcessor.class)
                    .addConstructorArgValue(annotationAttributes)
                    .getBeanDefinition();
            beanDefinition.setPrimary(true);
            registry.registerBeanDefinition("MapperBeanDefinitionProcessor", beanDefinition);
        }
    }

    private void registerBeanDefintionIfNotExist(BeanDefinitionRegistry registry, BeanDefinition beanDefinition, String beanName) {
        if (!registry.containsBeanDefinition(beanName)) {
            registry.registerBeanDefinition(beanName, beanDefinition);
        }
    }

    /**
     * 从配置文件读取DataSourceProperties 配置
     *
     * @param prefix
     * @param name
     * @return
     */
    private MultiDataSourceProperties readDataSourceProties(String prefix, String name) {
        String beanName = name;
        if (StringUtils.isEmpty(name)) {
            beanName = MapperBeanDefinitionProcessor.DEFAULT_DATASOURCE_NAME;
        } else {
            prefix = prefix + "." + name;
        }
        MultiDataSourceProperties properties = new MultiDataSourceProperties();
        properties.setBeanClassLoader(classLoader);
        properties.setName(beanName);
        try {
            Binder binder = Binder.get(environment);
            binder.bind(prefix, Bindable.ofInstance(properties));
            // properties.afterPropertiesSet();
        } catch (Exception e) {
            logger.error("没找到指定的jdbc配置信息{},尝试使用内存数据库", prefix);
        }
        return properties;
    }

    /**
     * 根据dataSourceProperties 创建BeanDefinition
     *
     * @param dataSourceProperties
     * @param primary
     * @return
     */
    private BeanDefinition buildBeanDefinition(DataSourceProperties dataSourceProperties, boolean primary) {
        BeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(DataSourceFactory.class)
                .addPropertyValue("properties", dataSourceProperties)
                .addPropertyValue("beanName", dataSourceProperties.getName())
                .getBeanDefinition();
        beanDefinition.setPrimary(primary);
        return beanDefinition;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
}
