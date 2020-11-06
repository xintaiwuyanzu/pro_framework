package com.dr.framework.core.orm.support.mybatis.spring.boot.autoconfigure;

import com.dr.framework.core.orm.support.mybatis.spring.DataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Set;

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
    private Binder binder;
    private Logger logger = LoggerFactory.getLogger(MapperBeanDefinitionProcessor.class);

    @Override
    public void registerBeanDefinitions(AnnotationMetadata classMetadata, BeanDefinitionRegistry registry) {
        String autoMapperClassName = EnableAutoMapper.class.getName();
        if (classMetadata.hasAnnotation(autoMapperClassName)) {
            String singleDataSourceName = null;
            AnnotationAttributes annotationAttributes = AnnotationAttributes.fromMap(classMetadata.getAnnotationAttributes(autoMapperClassName));
            AnnotationAttributes[] databases = annotationAttributes.getAnnotationArray("databases");
            if (databases.length == 0) {
                MultiDataSourceProperties dataSourceProperties = readDataSourceProties(MapperBeanDefinitionProcessor.DEFAULT_PREFIX, null);
                dataSourceProperties.setUseXa(false);
                BeanDefinition beanDefinition = buildBeanDefinition(dataSourceProperties, true);
                singleDataSourceName = dataSourceProperties.getName();
                registerBeanDefinitionIfNotExist(registry, beanDefinition, dataSourceProperties.getName());
            } else if (databases.length == 1) {
                AnnotationAttributes annotationAttributes1 = databases[0];
                MultiDataSourceProperties dataSourceProperties = readDataSourceProties(annotationAttributes1.getString("prefix"), annotationAttributes1.getString("name"));
                dataSourceProperties.setUseXa(false);
                BeanDefinition beanDefinition = buildBeanDefinition(dataSourceProperties, true);
                singleDataSourceName = dataSourceProperties.getName();
                registerBeanDefinitionIfNotExist(registry, beanDefinition, dataSourceProperties.getName());
            } else {
                Set<String> includedModules = new HashSet<>();
                for (AnnotationAttributes database : databases) {
                    boolean primary = database.getBoolean("primary");
                    MultiDataSourceProperties dataSourceProperties = readDataSourceProties(database.getString(PREFIX_KEY), database.getString("name"));
                    int setSize = includedModules.size();
                    includedModules.addAll(dataSourceProperties.getIncludeModules());
                    Assert.isTrue(setSize + dataSourceProperties.getIncludeModules().size() == includedModules.size()
                            , String.format("不得在多个数据源【%s】重复声明包含模块：%s"
                                    , dataSourceProperties.getName()
                                    , String.join(",", dataSourceProperties.getIncludeModules())
                            ));
                    BeanDefinition beanDefinition = buildBeanDefinition(dataSourceProperties, primary);
                    registerBeanDefinitionIfNotExist(registry, beanDefinition, dataSourceProperties.getName());
                }
            }
            BeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(MapperBeanDefinitionProcessor.class)
                    .addConstructorArgValue(annotationAttributes).setPrimary(true)
                    .getBeanDefinition();

            registry.registerBeanDefinition("MapperBeanDefinitionProcessor", beanDefinition);
            //如果只有一个数据源，则使用默认的事务管理器，多个的时候使用autoconfig
            if (singleDataSourceName != null) {
                BeanDefinition transactionBean = BeanDefinitionBuilder.genericBeanDefinition(DataSourceTransactionManager.class)
                        .addConstructorArgReference(singleDataSourceName)
                        .setPrimary(true)
                        .getBeanDefinition();
                registry.registerBeanDefinition("transactionManager", transactionBean);
            }
        }
    }

    private void registerBeanDefinitionIfNotExist(BeanDefinitionRegistry registry, BeanDefinition beanDefinition, String beanName) {
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
        try {
            MultiDataSourceProperties multiDataSourceProperties = new MultiDataSourceProperties();
            multiDataSourceProperties.setBeanClassLoader(classLoader);
            multiDataSourceProperties.setName(beanName);
            multiDataSourceProperties.setPrefix(prefix);
            //multiDataSourceProperties.afterPropertiesSet();
            return binder.bind(prefix, Bindable.ofInstance(multiDataSourceProperties)).get();
        } catch (Exception e) {
            logger.error("没找到指定的jdbc配置信息{},尝试使用内存数据库", prefix);
        }
        return null;
    }

    /**
     * 根据dataSourceProperties 创建BeanDefinition
     *
     * @param dataSourceProperties
     * @param primary
     * @return
     */
    private BeanDefinition buildBeanDefinition(MultiDataSourceProperties dataSourceProperties, boolean primary) {
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
        this.binder = Binder.get(environment);
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
}
