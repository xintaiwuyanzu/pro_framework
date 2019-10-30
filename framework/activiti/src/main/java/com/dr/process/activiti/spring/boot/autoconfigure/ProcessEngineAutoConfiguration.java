package com.dr.process.activiti.spring.boot.autoconfigure;

import com.dr.framework.core.organise.service.OrganisePersonService;
import com.dr.process.activiti.DefaultSecurityManager;
import com.dr.process.activiti.DefaultUserGroupManager;
import com.dr.process.activiti.spring.boot.validation.AsyncPropertyValidator;
import org.activiti.api.process.model.events.ProcessDeployedEvent;
import org.activiti.api.process.runtime.events.listener.ProcessRuntimeEventListener;
import org.activiti.api.runtime.shared.identity.UserGroupManager;
import org.activiti.api.runtime.shared.security.SecurityManager;
import org.activiti.core.common.spring.project.ProjectModelService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.cfg.ProcessEngineConfigurator;
import org.activiti.engine.impl.event.EventSubscriptionPayloadMappingProvider;
import org.activiti.engine.impl.persistence.StrongUuidGenerator;
import org.activiti.runtime.api.impl.VariablesMappingProvider;
import org.activiti.runtime.api.model.impl.APIProcessDefinitionConverter;
import org.activiti.spring.SpringAsyncExecutor;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.activiti.spring.process.ProcessVariablesInitiator;
import org.activiti.validation.ProcessValidatorImpl;
import org.activiti.validation.validator.ValidatorSet;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.*;

/**
 * activiti自动配置类
 * 因为activiti已经跟spring融合的很深了
 * 只要引用了这个jar包，启动了{@link org.springframework.boot.autoconfigure.EnableAutoConfiguration}
 * 就会自动配置
 * 没办法设置开关
 *
 * @author dr
 */
@Configuration
@ComponentScan("org.activiti.core.common.spring.security.policies.*")
@AutoConfigureAfter(TaskExecutionAutoConfiguration.class)
@EnableConfigurationProperties({ActivitiProperties.class, AsyncExecutorProperties.class})
public class ProcessEngineAutoConfiguration extends AbstractProcessEngineConfiguration implements ApplicationContextAware {
    public static final String BEHAVIOR_FACTORY_MAPPING_CONFIGURER = "behaviorFactoryMappingConfigurer";
    private ApplicationContext applicationContext;

    @Bean
    @ConditionalOnMissingBean
    public SecurityManager securityManager() {
        return new DefaultSecurityManager();
    }

    @Bean
    @ConditionalOnMissingBean
    public UserGroupManager userGroupManager(SecurityManager securityManager, OrganisePersonService organisePersonService) {
        return new DefaultUserGroupManager(securityManager, organisePersonService);
    }

    @Bean
    @ConditionalOnMissingBean
    public SpringProcessEngineConfiguration springProcessEngineConfiguration(
            PlatformTransactionManager transactionManager,
            SpringAsyncExecutor springAsyncExecutor,
            ActivitiProperties activitiProperties,
            ProcessDefinitionResourceFinder processDefinitionResourceFinder,
            ProjectModelService projectModelService,
            UserGroupManager userGroupManager,
            @Autowired(required = false) List<ProcessEngineConfigurationConfigurer> processEngineConfigurationConfigurers,
            @Autowired(required = false) List<ProcessEngineConfigurator> processEngineConfigurators) throws IOException {

        SpringProcessEngineConfiguration conf = new SpringProcessEngineConfiguration(projectModelService);
        conf.setConfigurators(processEngineConfigurators);
        configureProcessDefinitionResources(processDefinitionResourceFinder, conf);
        Map<String, DataSource> dataSources = applicationContext.getBeansOfType(DataSource.class);
        Assert.isTrue(!dataSources.isEmpty(), "未配置数据源！");
        int size = dataSources.size();
        if (size == 1) {
            conf.setDataSource(dataSources.values().iterator().next());
        } else {
            Assert.isTrue(!StringUtils.isEmpty(activitiProperties.getDatabaseName()), "请配置 spring.activiti.databaseName 属性指定流程引擎数据源！");
            DataSource dataSource = dataSources.get(activitiProperties.getDatabaseName());
            Assert.notNull(dataSource, "指定数据源名称【" + activitiProperties.getDatabaseName() + "】为空！");
            conf.setDataSource(dataSource);
        }
        conf.setTransactionManager(transactionManager);

        conf.setAsyncExecutor(springAsyncExecutor);
        conf.setDeploymentName(activitiProperties.getDeploymentName());
        conf.setDatabaseSchema(activitiProperties.getDatabaseSchema());
        conf.setDatabaseSchemaUpdate(activitiProperties.getDatabaseSchemaUpdate());
        conf.setDbHistoryUsed(activitiProperties.isDbHistoryUsed());
        conf.setAsyncExecutorActivate(activitiProperties.isAsyncExecutorActivate());
        addAsyncPropertyValidator(activitiProperties, conf);
        conf.setMailServerHost(activitiProperties.getMailServerHost());
        conf.setMailServerPort(activitiProperties.getMailServerPort());
        conf.setMailServerUsername(activitiProperties.getMailServerUserName());
        conf.setMailServerPassword(activitiProperties.getMailServerPassword());
        conf.setMailServerDefaultFrom(activitiProperties.getMailServerDefaultFrom());
        conf.setMailServerUseSSL(activitiProperties.isMailServerUseSsl());
        conf.setMailServerUseTLS(activitiProperties.isMailServerUseTls());
        conf.setUserGroupManager(userGroupManager);

        conf.setHistoryLevel(activitiProperties.getHistoryLevel());
        conf.setCopyVariablesToLocalForTasks(activitiProperties.isCopyVariablesToLocalForTasks());
        conf.setSerializePOJOsInVariablesToJson(activitiProperties.isSerializePOJOsInVariablesToJson());
        conf.setJavaClassFieldForJackson(activitiProperties.getJavaClassFieldForJackson());

        if (activitiProperties.getCustomMybatisMappers() != null) {
            conf.setCustomMybatisMappers(getCustomMybatisMapperClasses(activitiProperties.getCustomMybatisMappers()));
        }

        if (activitiProperties.getCustomMybatisXMLMappers() != null) {
            conf.setCustomMybatisXMLMappers(new HashSet<>(activitiProperties.getCustomMybatisXMLMappers()));
        }

        if (activitiProperties.getCustomMybatisXMLMappers() != null) {
            conf.setCustomMybatisXMLMappers(new HashSet<>(activitiProperties.getCustomMybatisXMLMappers()));
        }

        if (activitiProperties.isUseStrongUuids()) {
            conf.setIdGenerator(new StrongUuidGenerator());
        }

        if (activitiProperties.getDeploymentMode() != null) {
            conf.setDeploymentMode(activitiProperties.getDeploymentMode());
        }

        if (processEngineConfigurationConfigurers != null) {
            for (ProcessEngineConfigurationConfigurer processEngineConfigurationConfigurer : processEngineConfigurationConfigurers) {
                processEngineConfigurationConfigurer.configure(conf);
            }
        }
        springAsyncExecutor.applyConfig(conf);
        return conf;
    }

    protected Set<Class<?>> getCustomMybatisMapperClasses(List<String> customMyBatisMappers) {
        Set<Class<?>> mybatisMappers = new HashSet<>();
        for (String customMybatisMapperClassName : customMyBatisMappers) {
            try {
                Class customMybatisClass = Class.forName(customMybatisMapperClassName);
                mybatisMappers.add(customMybatisClass);
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException("Class " + customMybatisMapperClassName + " has not been found.", e);
            }
        }
        return mybatisMappers;
    }

    protected void addAsyncPropertyValidator(ActivitiProperties activitiProperties, SpringProcessEngineConfiguration conf) {
        if (!activitiProperties.isAsyncExecutorActivate()) {
            ValidatorSet springBootStarterValidatorSet = new ValidatorSet("activiti-spring-boot-starter");
            springBootStarterValidatorSet.addValidator(new AsyncPropertyValidator());
            if (conf.getProcessValidator() == null) {
                ProcessValidatorImpl processValidator = new ProcessValidatorImpl();
                processValidator.addValidatorSet(springBootStarterValidatorSet);
                conf.setProcessValidator(processValidator);
            } else {
                conf.getProcessValidator().getValidatorSets().add(springBootStarterValidatorSet);
            }
        }
    }

    private void configureProcessDefinitionResources(ProcessDefinitionResourceFinder processDefinitionResourceFinder,
                                                     SpringProcessEngineConfiguration conf) throws IOException {
        List<Resource> procDefResources = processDefinitionResourceFinder.discoverProcessDefinitionResources();
        if (!procDefResources.isEmpty()) {
            conf.setDeploymentResources(procDefResources.toArray(new Resource[0]));
        }
    }

    @Bean
    @ConditionalOnMissingBean
    public ProcessDefinitionResourceFinder processDefinitionResourceFinder(ActivitiProperties activitiProperties,
                                                                           ResourcePatternResolver resourcePatternResolver) {
        return new ProcessDefinitionResourceFinder(activitiProperties,
                resourcePatternResolver);
    }

    @Bean
    @ConditionalOnMissingBean
    public ProcessDeployedEventProducer processDeployedEventProducer(RepositoryService repositoryService,
                                                                     APIProcessDefinitionConverter converter,
                                                                     @Autowired(required = false) List<ProcessRuntimeEventListener<ProcessDeployedEvent>> listeners,
                                                                     ApplicationEventPublisher eventPublisher) {
        return new ProcessDeployedEventProducer(repositoryService,
                converter,
                Optional.ofNullable(listeners)
                        .orElse(Collections.emptyList()),
                eventPublisher);
    }

    @Bean(name = BEHAVIOR_FACTORY_MAPPING_CONFIGURER)
    @ConditionalOnMissingBean(name = BEHAVIOR_FACTORY_MAPPING_CONFIGURER)
    public DefaultActivityBehaviorFactoryMappingConfigurer defaultActivityBehaviorFactoryMappingConfigurer(VariablesMappingProvider variablesMappingProvider,
                                                                                                           ProcessVariablesInitiator processVariablesInitiator,
                                                                                                           EventSubscriptionPayloadMappingProvider eventSubscriptionPayloadMappingProvider) {
        return new DefaultActivityBehaviorFactoryMappingConfigurer(variablesMappingProvider,
                processVariablesInitiator,
                eventSubscriptionPayloadMappingProvider);
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public ProcessEngineConfigurationConfigurer asyncExecutorPropertiesConfigurer(AsyncExecutorProperties properties) {
        return (configuration) -> {
            configuration.setAsyncExecutorMessageQueueMode(properties.isMessageQueueMode());
            configuration.setAsyncExecutorCorePoolSize(properties.getCorePoolSize());
            configuration.setAsyncExecutorAsyncJobLockTimeInMillis(properties.getAsyncJobLockTimeInMillis());
            configuration.setAsyncExecutorNumberOfRetries(properties.getNumberOfRetries());

            configuration.setAsyncExecutorDefaultAsyncJobAcquireWaitTime(properties.getDefaultAsyncJobAcquireWaitTimeInMillis());
            configuration.setAsyncExecutorDefaultTimerJobAcquireWaitTime(properties.getDefaultTimerJobAcquireWaitTimeInMillis());
            configuration.setAsyncExecutorDefaultQueueSizeFullWaitTime(properties.getDefaultQueueSizeFullWaitTime());

            configuration.setAsyncExecutorMaxAsyncJobsDuePerAcquisition(properties.getMaxAsyncJobsDuePerAcquisition());
            configuration.setAsyncExecutorMaxTimerJobsPerAcquisition(properties.getMaxTimerJobsPerAcquisition());
            configuration.setAsyncExecutorMaxPoolSize(properties.getMaxPoolSize());

            configuration.setAsyncExecutorResetExpiredJobsInterval(properties.getResetExpiredJobsInterval());
            configuration.setAsyncExecutorResetExpiredJobsPageSize(properties.getResetExpiredJobsPageSize());

            configuration.setAsyncExecutorSecondsToWaitOnShutdown(properties.getSecondsToWaitOnShutdown());
            configuration.setAsyncExecutorThreadKeepAliveTime(properties.getKeepAliveTime());
            configuration.setAsyncExecutorTimerLockTimeInMillis(properties.getTimerLockTimeInMillis());
            configuration.setAsyncExecutorThreadPoolQueueSize(properties.getQueueSize());

            configuration.setAsyncFailedJobWaitTime(properties.getRetryWaitTimeInMillis());
        };
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
