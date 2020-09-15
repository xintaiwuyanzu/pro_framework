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
    /**
     * 是否调用{@link com.dr.framework.sys.service.InitDataService} 自动初始化数据
     */
    private boolean autoInitData = false;

    public String getApiPath() {
        return apiPath;
    }

    public void setApiPath(String apiPath) {
        this.apiPath = apiPath;
    }

    public boolean isAutoInitData() {
        return autoInitData;
    }

    public void setAutoInitData(boolean autoInitData) {
        this.autoInitData = autoInitData;
    }
}
