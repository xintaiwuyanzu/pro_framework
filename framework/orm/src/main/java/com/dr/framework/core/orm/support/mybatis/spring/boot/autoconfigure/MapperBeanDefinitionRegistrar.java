package com.dr.framework.core.orm.support.mybatis.spring.boot.autoconfigure;

import com.dr.framework.core.orm.support.mybatis.spring.DataSourceFactory;
import com.dr.framework.core.orm.support.mybatis.spring.MybatisConfigurationBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.Aware;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.*;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 用来注入自定义规则的mapper接口
 */
public class MapperBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware, BeanClassLoaderAware, ResourceLoaderAware, BeanFactoryAware {
    Logger logger = LoggerFactory.getLogger(MapperBeanDefinitionRegistrar.class);
    public final static String DEFAULT_PREFIX = "spring.datasource";
    final static String DEFAULT_DATASOURCE_NAME = "datasource";
    final static String AUTO_MAPPER_CLASS_NAME = EnableAutoMapper.class.getName();
    private Environment environment;
    private ClassLoader classLoader;
    private BeanFactory beanFactory;
    private ResourceLoader resourceLoader;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata classMetadata, BeanDefinitionRegistry registry) {
        if (classMetadata.hasAnnotation(AUTO_MAPPER_CLASS_NAME)) {
            AnnotationAttributes attributes = AnnotationAttributes.fromMap(classMetadata.getAnnotationAttributes(AUTO_MAPPER_CLASS_NAME));
            //注册各个mapper接口
            List<String> mapperInterfaces = registerMappers(registry, attributes);
            AnnotationAttributes[] databases = attributes.getAnnotationArray("databases");
            boolean isSingleDataSource = true;
            if (databases.length == 0) {
                registerDefaultOrEmberDataSource(registry, DEFAULT_PREFIX, null, mapperInterfaces);
            } else if (databases.length == 1) {
                AnnotationAttributes annotationAttributes = databases[0];
                registerDefaultOrEmberDataSource(registry, annotationAttributes.getString("prefix"), annotationAttributes.getString("name"), mapperInterfaces);
            } else {
                isSingleDataSource = false;
                for (int i = 0; i < databases.length; i++) {
                    AnnotationAttributes database = databases[i];
                    registerSpecifyedDataSource(registry, database, mapperInterfaces);
                }
            }
            registerTransactionManager(registry, isSingleDataSource);
        }
    }

    /**
     * 注册默认的jdbc  bean 或者内置的内存数据库
     *
     * @param registry
     * @param prefix
     * @param name
     */
    private void registerDefaultOrEmberDataSource(BeanDefinitionRegistry registry, String prefix, String name, List<String> mapperInterfaces) {
        DataSourceProperties dataSourceProperties = readDataSourceProties(prefix, name);
        BeanDefinition beanDefinition = buildBeanDefinition(dataSourceProperties, false, true);
        registerBeanDefintionIfNotExist(registry, beanDefinition, dataSourceProperties.getName());

        registerMybatisConfigs(registry, dataSourceProperties, null, true, mapperInterfaces);
    }


    /**
     * 根据声明的信息从配置文件读取配置信息并注册相关的数据库
     *
     * @param registry
     * @param database
     * @param mapperInterfaces
     */
    private void registerSpecifyedDataSource(BeanDefinitionRegistry registry, AnnotationAttributes database, List<String> mapperInterfaces) {
        boolean primary = database.getBoolean("primary");
        boolean xa = true;
        try {
            xa = database.getBoolean("xa");
        } catch (Exception e) {
        }
        DataSourceProperties dataSourceProperties = readDataSourceProties(database.getString("prefix"), database.getString("name"));
        BeanDefinition beanDefinition = buildBeanDefinition(dataSourceProperties, xa, primary);
        registerBeanDefintionIfNotExist(registry, beanDefinition, dataSourceProperties.getName());

        registerMybatisConfigs(registry, dataSourceProperties, database, primary, mapperInterfaces);
    }

    /**
     * 指定数据库创建mybatis相关的bean
     *
     * @param registry
     * @param dataSourceProperties
     * @param databaseAttributes
     * @param primary
     * @param mapperInterfaces
     */
    private void registerMybatisConfigs(BeanDefinitionRegistry registry, DataSourceProperties dataSourceProperties, AnnotationAttributes databaseAttributes, boolean primary, List<String> mapperInterfaces) {
        ClassPathEntityScanner classPathEntityScanner = new ClassPathEntityScanner(registry);
        setScanner(classPathEntityScanner, databaseAttributes);
        String propertyName;
        if (databaseAttributes != null) {
            propertyName = String.join(".", databaseAttributes.getString("prefix"), databaseAttributes.getString("name"));
            classPathEntityScanner.addModuleInclude(databaseAttributes.getStringArray("includeModules"));
            classPathEntityScanner.addModuleExclude(databaseAttributes.getStringArray("excludeModules"));
        } else {
            propertyName = DEFAULT_PREFIX;
        }
        String includeModules = environment.getProperty(propertyName + ".includeModules");
        if (!StringUtils.isEmpty(includeModules)) {
            classPathEntityScanner.addModuleInclude(includeModules.split(","));
        }
        String excludeModules = environment.getProperty(propertyName + ".excludeModules");
        if (!StringUtils.isEmpty(excludeModules)) {
            classPathEntityScanner.addModuleExclude(includeModules.split(","));
        }
        classPathEntityScanner.registerFilters();
        List<String> pkgs = readPackgeList(databaseAttributes);
        if (pkgs.size() == 0) {
            pkgs = AutoConfigurationPackages.get(this.beanFactory);
        }
        List<String> entityClass = classPathEntityScanner.scan(pkgs);

        String configBeanName = MybatisConfigurationBean.class.getName() + "." + dataSourceProperties.getName();

        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(MybatisConfigurationBean.class)
                .addPropertyValue("databaseId", dataSourceProperties.getName())
                .addPropertyValue("mapperInterfaces", mapperInterfaces)
                .addPropertyValue("entityClass", entityClass)
                .addPropertyValue("modules", includeModules.split(","))
                .addPropertyValue("dataSourceProperties", dataSourceProperties)
                .addPropertyReference("dataSource", dataSourceProperties.getName());

        String autoDDl = environment.getProperty(propertyName + ".autoDDl");
        if (!StringUtils.isEmpty(autoDDl)) {
            builder.addPropertyValue("autoDDl", autoDDl);
        }
        BeanDefinition beanDefinition = builder.getBeanDefinition();
        beanDefinition.setPrimary(primary);
        registerBeanDefintionIfNotExist(registry, beanDefinition, configBeanName);
    }


    /**
     * 注册事务管理bean
     *
     * @param registry
     * @param isSingleDataSource
     */
    private void registerTransactionManager(BeanDefinitionRegistry registry, boolean isSingleDataSource) {
    }

    /**
     * 动态创建各个mapper bean
     *
     * @param registry
     * @param attributes
     */
    private List<String> registerMappers(BeanDefinitionRegistry registry, AnnotationAttributes attributes) {
        List<String> applicationPackages = AutoConfigurationPackages.get(this.beanFactory);
        applicationPackages.addAll(readPackgeList(attributes));

        ClassPathMapperScanner classPathMapperScanner = new ClassPathMapperScanner(registry);
        setScanner(classPathMapperScanner, attributes);

        Set<BeanDefinitionHolder> beanDefinitionHolders = classPathMapperScanner.doScan(applicationPackages.toArray(new String[applicationPackages.size()]));
        return beanDefinitionHolders.stream().map(beanDefinitionHolder -> beanDefinitionHolder.getBeanDefinition().getPropertyValues().get("mapperInterface").toString()).collect(Collectors.toList());
    }

    /**
     * 从配置文件读取DataSourceProperties 配置
     *
     * @param prefix
     * @param name
     * @return
     */
    private DataSourceProperties readDataSourceProties(String prefix, String name) {
        String beanName = name;
        if (StringUtils.isEmpty(name)) {
            beanName = DEFAULT_DATASOURCE_NAME;
        } else {
            prefix = prefix + "." + name;
        }
        DataSourceProperties properties = new DataSourceProperties();
        properties.setBeanClassLoader(classLoader);
        properties.setName(beanName);
        try {
            properties.setUrl(environment.getRequiredProperty(prefix + ".url"));
            properties.setUsername(environment.getRequiredProperty(prefix + ".username"));
            properties.setPassword(environment.getRequiredProperty(prefix + ".password"));
            properties.afterPropertiesSet();
        } catch (Exception e) {
            logger.error("没找到指定的jdbc配置信息{},尝试使用内存数据库", prefix);
        }
        return properties;
    }

    /**
     * 根据dataSourceProperties 创建BeanDefinition
     *
     * @param dataSourceProperties
     * @param xa
     * @param primary
     * @return
     */
    private BeanDefinition buildBeanDefinition(DataSourceProperties dataSourceProperties, boolean xa, boolean primary) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(DataSourceFactory.class)
                .addPropertyValue("properties", dataSourceProperties)
                .addPropertyValue("beanName", dataSourceProperties.getName())
                .addPropertyValue("xa", xa);
        BeanDefinition beanDefinition = builder.getBeanDefinition();
        beanDefinition.setPrimary(primary);
        return beanDefinition;
    }

    private void registerBeanDefintionIfNotExist(BeanDefinitionRegistry registry, BeanDefinition beanDefinition, String beanName) {
        if (!registry.containsBeanDefinition(beanName)) {
            registry.registerBeanDefinition(beanName, beanDefinition);
        }
    }

    private void setScanner(ClassPathBeanDefinitionScanner beanDefinitionScanner, AnnotationAttributes databaseAttributes) {
        beanDefinitionScanner.setResourceLoader(this.resourceLoader);
        beanDefinitionScanner.setEnvironment(environment);
        if (databaseAttributes != null) {
            List<TypeFilter> includeFilters = readTypeFilters(beanDefinitionScanner.getRegistry(), databaseAttributes, "includeFilters");
            for (TypeFilter typeFilter : includeFilters) {
                beanDefinitionScanner.addIncludeFilter(typeFilter);
            }
            List<TypeFilter> excludeFilters = readTypeFilters(beanDefinitionScanner.getRegistry(), databaseAttributes, "excludeFilters");
            for (TypeFilter typeFilter : excludeFilters) {
                beanDefinitionScanner.addExcludeFilter(typeFilter);
            }
        }
    }

    private List<TypeFilter> readTypeFilters(BeanDefinitionRegistry registry, AnnotationAttributes attributes, String filterName) {
        List<TypeFilter> typeFilters = new ArrayList<>();
        for (AnnotationAttributes filter : attributes.getAnnotationArray(filterName)) {
            for (TypeFilter typeFilter : typeFiltersFor(filter, registry)) {
                typeFilters.add(typeFilter);
            }
        }
        return typeFilters;
    }

    private List<String> readPackgeList(AnnotationAttributes attributes) {
        List<String> list = new ArrayList<>();
        if (attributes != null) {
            for (String pkg : attributes.getStringArray("value")) {
                list.add(pkg);
            }
            for (Class clazz : attributes.getClassArray("basePackageClasses")) {
                list.add(ClassUtils.getPackageName(clazz));
            }
        }
        return list;
    }

    private List<TypeFilter> typeFiltersFor(AnnotationAttributes filterAttributes, BeanDefinitionRegistry registry) {
        List<TypeFilter> typeFilters = new ArrayList<>();
        FilterType filterType = filterAttributes.getEnum("type");
        for (Class<?> filterClass : filterAttributes.getClassArray("classes")) {
            switch (filterType) {
                case ANNOTATION:
                    Assert.isAssignable(Annotation.class, filterClass,
                            "@ComponentScan ANNOTATION type filter requires an annotations type");
                    @SuppressWarnings("unchecked")
                    Class<Annotation> annotationType = (Class<Annotation>) filterClass;
                    typeFilters.add(new AnnotationTypeFilter(annotationType));
                    break;
                case ASSIGNABLE_TYPE:
                    typeFilters.add(new AssignableTypeFilter(filterClass));
                    break;
                case CUSTOM:
                    Assert.isAssignable(TypeFilter.class, filterClass, "@ComponentScan CUSTOM type filter requires a TypeFilter implementation");
                    TypeFilter filter = BeanUtils.instantiateClass(filterClass, TypeFilter.class);
                    invokeAwareMethods(filter, this.environment, this.resourceLoader, registry);
                    typeFilters.add(filter);
                    break;
                default:
                    throw new IllegalArgumentException("Filter type not supported with Class value: " + filterType);
            }
        }
        for (String expression : filterAttributes.getStringArray("pattern")) {
            switch (filterType) {
                case ASPECTJ:
                    typeFilters.add(new AspectJTypeFilter(expression, this.resourceLoader.getClassLoader()));
                    break;
                case REGEX:
                    typeFilters.add(new RegexPatternTypeFilter(Pattern.compile(expression)));
                    break;
                default:
                    throw new IllegalArgumentException("Filter type not supported with String pattern: " + filterType);
            }
        }

        return typeFilters;
    }

    void invokeAwareMethods(Object parserStrategyBean, Environment environment, ResourceLoader resourceLoader, BeanDefinitionRegistry registry) {
        if (parserStrategyBean instanceof Aware) {
            if (parserStrategyBean instanceof BeanClassLoaderAware) {
                ClassLoader classLoader = (registry instanceof ConfigurableBeanFactory ?
                        ((ConfigurableBeanFactory) registry).getBeanClassLoader() : resourceLoader.getClassLoader());
                if (classLoader != null) {
                    ((BeanClassLoaderAware) parserStrategyBean).setBeanClassLoader(classLoader);
                }
            }
            if (parserStrategyBean instanceof BeanFactoryAware && registry instanceof BeanFactory) {
                ((BeanFactoryAware) parserStrategyBean).setBeanFactory((BeanFactory) registry);
            }
            if (parserStrategyBean instanceof EnvironmentAware) {
                ((EnvironmentAware) parserStrategyBean).setEnvironment(environment);
            }
            if (parserStrategyBean instanceof ResourceLoaderAware) {
                ((ResourceLoaderAware) parserStrategyBean).setResourceLoader(resourceLoader);
            }
        }
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }


    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
