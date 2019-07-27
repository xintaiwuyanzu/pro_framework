package com.dr.framework.core.orm.support.mybatis.spring.boot.autoconfigure;

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
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.filter.*;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author dr
 */
public class MapperBeanDefinitionProcessor implements BeanDefinitionRegistryPostProcessor, EnvironmentAware, ResourceLoaderAware, BeanFactoryAware {
    /**
     * 注解前缀key
     */
    protected static final String PREFIX_KEY = "prefix";
    /**
     * 默认数据库属性配置key
     */
    static final String DEFAULT_PREFIX = "spring.datasource";
    /**
     * 默认数据库配置bean名称
     */
    static final String DEFAULT_DATASOURCE_NAME = "datasource";
    /**
     * 注解类名称
     */
    private Logger logger = LoggerFactory.getLogger(MapperBeanDefinitionProcessor.class);
    /**
     * 上下信息，用来获取配置属性
     */
    private Environment environment;
    /**
     * 用来获取当前上下文默认的package
     */
    private BeanFactory beanFactory;
    /**
     * 用来扫描所有的class文件
     */
    private ResourceLoader resourceLoader;
    /**
     * 注解类的注解属性
     */
    private AnnotationAttributes annotationAttributes;

    List<String> mapperInterfaces;
    List<String> mybatisConfigNames = new ArrayList<>();

    public MapperBeanDefinitionProcessor(AnnotationAttributes annotationAttributes) {
        this.annotationAttributes = annotationAttributes;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        //注册各个mapper接口
        mapperInterfaces = registerMappers(registry, annotationAttributes);
        AnnotationAttributes[] databases = annotationAttributes.getAnnotationArray("databases");
        if (databases.length == 0) {
            MultiDataSourceProperties dataSourceProperties = readDataSourceProties(DEFAULT_PREFIX, null);
            registerMybatisConfigs(registry, dataSourceProperties, annotationAttributes);
        } else if (databases.length == 1) {
            AnnotationAttributes annotationAttributes = databases[0];
            MultiDataSourceProperties dataSourceProperties = readDataSourceProties(annotationAttributes.getString("prefix"), annotationAttributes.getString("name"));
            registerMybatisConfigs(registry, dataSourceProperties, annotationAttributes);
        } else {
            for (int i = 0; i < databases.length; i++) {
                AnnotationAttributes database = databases[i];
                registerSpecifyedDataSource(registry, database);
            }
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        for (String beanName : mybatisConfigNames) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
            beanDefinition.getPropertyValues().add("mapperInterfaces", mapperInterfaces);
        }
    }


    /**
     * 根据声明的信息从配置文件读取配置信息并注册相关的数据库
     *
     * @param registry
     * @param database
     */
    private void registerSpecifyedDataSource(BeanDefinitionRegistry registry, AnnotationAttributes database) {
        MultiDataSourceProperties dataSourceProperties = readDataSourceProties(database.getString(PREFIX_KEY), database.getString("name"));
        registerMybatisConfigs(registry, dataSourceProperties, database);
    }

    /**
     * 指定数据库创建mybatis相关的bean
     *
     * @param registry
     * @param dataSourceProperties
     * @param databaseAttributes
     */
    private void registerMybatisConfigs(BeanDefinitionRegistry registry, MultiDataSourceProperties dataSourceProperties, AnnotationAttributes databaseAttributes) {
        //先扫描所有的实体类
        boolean primary = true;
        ClassPathEntityScanner classPathEntityScanner = new ClassPathEntityScanner(registry, dataSourceProperties);
        setScanner(classPathEntityScanner, databaseAttributes);
        if (databaseAttributes != null && databaseAttributes.containsKey("primary")) {
            primary = databaseAttributes.getBoolean("primary");
        }
        List<String> pkgs = readPackgeList(databaseAttributes);
        if (pkgs.isEmpty()) {
            pkgs = AutoConfigurationPackages.get(this.beanFactory);
        }
        List<String> entityClass = classPathEntityScanner.scan(pkgs);
        //在创建mybatisConfig
        BeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(MybatisConfigurationBean.class)
                .addPropertyValue("entityClass", entityClass)
                .addPropertyValue("dataSourceProperties", dataSourceProperties)
                .getBeanDefinition();
        beanDefinition.setPrimary(primary);
        String beanName = MybatisConfigurationBean.class.getName() + "." + dataSourceProperties.getName();
        mybatisConfigNames.add(beanName);
        registerBeanDefintionIfNotExist(registry, beanDefinition, beanName);
    }

    /**
     * 动态创建各个mapper bean
     *
     * @param registry
     * @param attributes
     */
    private List<String> registerMappers(BeanDefinitionRegistry registry, AnnotationAttributes attributes) {
        List<String> pkgs = readPackgeList(attributes);
        if (pkgs.isEmpty()) {
            pkgs = AutoConfigurationPackages.get(this.beanFactory);
        }
        ClassPathMapperScanner classPathMapperScanner = new ClassPathMapperScanner(registry);
        setScanner(classPathMapperScanner, attributes);

        classPathMapperScanner.scan(pkgs.toArray(new String[pkgs.size()]));
        return classPathMapperScanner.getInterfaces();
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
            beanName = DEFAULT_DATASOURCE_NAME;
        } else {
            prefix = prefix + "." + name;
        }
        MultiDataSourceProperties properties = new MultiDataSourceProperties();
        properties.setBeanClassLoader(resourceLoader.getClassLoader());
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
                ClassLoader beanCLassLoader = (registry instanceof ConfigurableBeanFactory ? ((ConfigurableBeanFactory) registry).getBeanClassLoader() : resourceLoader.getClassLoader());
                if (beanCLassLoader != null) {
                    ((BeanClassLoaderAware) parserStrategyBean).setBeanClassLoader(beanCLassLoader);
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
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
