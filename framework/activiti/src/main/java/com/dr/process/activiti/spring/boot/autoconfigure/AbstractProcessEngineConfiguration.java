package com.dr.process.activiti.spring.boot.autoconfigure;

import org.activiti.engine.*;
import org.activiti.engine.impl.persistence.entity.integration.IntegrationContextManager;
import org.activiti.engine.integration.IntegrationContextService;
import org.activiti.spring.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;

/**
 * 抽象定义，用来理清使用activiti
 * 所需要配置的所有的选项
 *
 * @author dr
 */
public abstract class AbstractProcessEngineConfiguration {

    /**
     * 用来构造 {@link ProcessEngine}对象
     *
     * @param configuration
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public ProcessEngineFactoryBean springProcessEngineBean(SpringProcessEngineConfiguration configuration) {
        ProcessEngineFactoryBean processEngineFactoryBean = new ProcessEngineFactoryBean();
        processEngineFactoryBean.setProcessEngineConfiguration(configuration);
        return processEngineFactoryBean;
    }

    @Bean
    public SpringAsyncExecutor springAsyncExecutor(TaskExecutor applicationTaskExecutor,
                                                   SpringRejectedJobsHandler springRejectedJobsHandler) {
        return new SpringAsyncExecutor(applicationTaskExecutor, springRejectedJobsHandler);
    }

    @Bean
    public SpringRejectedJobsHandler springRejectedJobsHandler() {
        return new SpringCallerRunsRejectedJobsHandler();
    }


    /**=========================================================
     *以下方法都是暴漏processEngine内部变量到spring上下文的配置方法，可以不用关注
     ===========================================================*/
    /**
     * RuntimeService 供项目使用，实际上初始化的时候是被processEngine 所管理
     * <p>
     * 这里直接取出来暴漏给spring上下文
     *
     * @param processEngine
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public RuntimeService runtimeServiceBean(ProcessEngine processEngine) {
        return processEngine.getRuntimeService();
    }

    /**
     * 同上
     *
     * @param processEngine
     * @return
     * @see #runtimeServiceBean(ProcessEngine)
     */
    @Bean
    @ConditionalOnMissingBean
    public RepositoryService repositoryServiceBean(ProcessEngine processEngine) {
        return processEngine.getRepositoryService();
    }

    /**
     * 同上
     *
     * @param processEngine
     * @return
     * @see #runtimeServiceBean(ProcessEngine)
     */
    @Bean
    @ConditionalOnMissingBean
    public TaskService taskServiceBean(ProcessEngine processEngine) {
        return processEngine.getTaskService();
    }

    /**
     * 同上
     *
     * @param processEngine
     * @return
     * @see #runtimeServiceBean(ProcessEngine)
     */
    @Bean
    @ConditionalOnMissingBean
    public HistoryService historyServiceBean(ProcessEngine processEngine) {
        return processEngine.getHistoryService();
    }

    /**
     * 同上
     *
     * @param processEngine
     * @return
     * @see #runtimeServiceBean(ProcessEngine)
     */
    @Bean
    @ConditionalOnMissingBean
    public ManagementService managementServiceBeanBean(ProcessEngine processEngine) {
        return processEngine.getManagementService();
    }

    /**
     * 同上
     *
     * @param processEngine
     * @return
     * @see #runtimeServiceBean(ProcessEngine)
     */
    @Bean
    @ConditionalOnMissingBean
    public IntegrationContextManager integrationContextManagerBean(ProcessEngine processEngine) {
        return processEngine.getProcessEngineConfiguration().getIntegrationContextManager();
    }

    /**
     * 同上
     *
     * @param processEngine
     * @return
     * @see #runtimeServiceBean(ProcessEngine)
     */
    @Bean
    @ConditionalOnMissingBean
    public IntegrationContextService integrationContextServiceBean(ProcessEngine processEngine) {
        return processEngine.getProcessEngineConfiguration().getIntegrationContextService();
    }

}
