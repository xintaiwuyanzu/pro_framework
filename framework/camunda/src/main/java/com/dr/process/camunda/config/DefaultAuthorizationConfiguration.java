package com.dr.process.camunda.config;

import org.camunda.bpm.engine.impl.cfg.auth.ResourceAuthorizationProvider;
import org.camunda.bpm.engine.spring.SpringProcessEngineConfiguration;

public class DefaultAuthorizationConfiguration extends org.camunda.bpm.spring.boot.starter.configuration.impl.DefaultAuthorizationConfiguration {

    private ResourceAuthorizationProvider resourceAuthorizationProvider;

    public DefaultAuthorizationConfiguration(ResourceAuthorizationProvider resourceAuthorizationProvider) {
        this.resourceAuthorizationProvider = resourceAuthorizationProvider;
    }

    @Override
    public void preInit(SpringProcessEngineConfiguration configuration) {
        super.preInit(configuration);
        if (resourceAuthorizationProvider != null) {
            configuration.setAuthorizationEnabled(true);
            configuration.setResourceAuthorizationProvider(resourceAuthorizationProvider);
        }
    }
}
