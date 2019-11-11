package com.dr.process.activiti.spring.boot.autoconfigure;

import org.activiti.spring.SpringProcessEngineConfiguration;

public interface ProcessEngineConfigurationConfigurer {
    void configure(SpringProcessEngineConfiguration processEngineConfiguration);
}
