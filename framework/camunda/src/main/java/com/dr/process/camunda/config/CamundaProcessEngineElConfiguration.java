package com.dr.process.camunda.config;

import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.el.ExpressionManager;
import org.camunda.bpm.engine.impl.javax.el.CompositeELResolver;
import org.camunda.bpm.engine.impl.javax.el.ELResolver;
import org.camunda.bpm.engine.spring.SpringExpressionManager;
import org.camunda.bpm.spring.boot.starter.configuration.CamundaProcessEngineConfiguration;
import org.springframework.context.ApplicationContext;

import java.util.Map;

public class CamundaProcessEngineElConfiguration implements CamundaProcessEngineConfiguration {
    private ELResolver[] resolvers;
    private ApplicationContext applicationContext;

    public CamundaProcessEngineElConfiguration(ApplicationContext applicationContext, ELResolver... elResolver) {
        this.resolvers = elResolver;
        this.applicationContext = applicationContext;
    }

    @Override
    public void preInit(ProcessEngineConfigurationImpl processEngineConfiguration) {
        ExpressionManager manager = processEngineConfiguration.getExpressionManager();
        if (manager instanceof SpringExpressionManager) {
            processEngineConfiguration.setExpressionManager(
                    new CustomerExpressionManager(applicationContext, processEngineConfiguration.getBeans())
            );
        }
    }


    class CustomerExpressionManager extends SpringExpressionManager {
        public CustomerExpressionManager(ApplicationContext applicationContext, Map<Object, Object> beans) {
            super(applicationContext, beans);
        }

        @Override
        protected org.camunda.bpm.engine.impl.javax.el.ELResolver createElResolver() {
            CompositeELResolver elResolver = (CompositeELResolver) super.createElResolver();
            if (resolvers != null) {
                for (ELResolver elResolver1 : resolvers) {
                    elResolver.add(elResolver1);
                }
            }
            return elResolver;
        }
    }


}
