package com.dr.framework.autoconfig;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 配置默认常用属性
 *
 * @author dr
 */
@ConfigurationProperties(prefix = "common")
public class CommonConfig {
    /**
     * api访问路径
     */
    private String apiPath = "/api";

    public String getApiPath() {
        return apiPath;
    }

    public void setApiPath(String apiPath) {
        this.apiPath = apiPath;
    }
}
