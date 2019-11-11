package com.dr.framework.autoconfig;

import com.dr.framework.task.SecurityHolderTaskDecorator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 启动异步相关的配置，主要是在异步线程注入当前操作人相关的信息
 * <p>
 * spring boot 默认实现是{@link org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration}
 *
 * @author dr
 */
@EnableAsync
@Configuration
public class TaskAutoConfiguration {
    @Bean
    public SecurityHolderTaskDecorator securityHolderTaskDecorator() {
        return new SecurityHolderTaskDecorator();
    }
}
