package com.dr.framework.common.config.service;

import com.dr.framework.common.config.entity.CommonConfig;

import java.util.List;

/**
 * 通用配置类转换工厂
 *
 * @author dr
 */
public interface CommonConfigBeanFactory {
    /**
     * 将list config 转换成list bean
     *
     * @param configs     查询出来的
     * @param configClass
     * @param <T>
     * @return
     */
    <T> List<T> newConfigBean(List<CommonConfig> configs, Class<T> configClass);

    /**
     * 根据带有注解的bean创建一组
     * {@link CommonConfig}
     *
     * @param configBean
     * @param <T>
     * @return
     */
    <T> List<CommonConfig> newConfigs(T configBean);

}
