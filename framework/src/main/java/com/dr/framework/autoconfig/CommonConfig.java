package com.dr.framework.autoconfig;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashSet;
import java.util.Set;

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
    /**
     * 全局登陆账户默认登录密码
     */
    private String defaultPassWord = "A123456";
    /**
     * 是否启用验证码登录验证
     */
    private boolean enableCaptcha = false;
    /**
     * 是否使用sessionId作为token
     */
    private boolean sessionAuth = true;
    /**
     * 系统支持的文件上传路径
     */
    private Set<String> fileUploadUrls = new HashSet<>();

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

    public String getDefaultPassWord() {
        return defaultPassWord;
    }

    public void setDefaultPassWord(String defaultPassWord) {
        this.defaultPassWord = defaultPassWord;
    }

    public boolean isEnableCaptcha() {
        return enableCaptcha;
    }

    public void setEnableCaptcha(boolean enableCaptcha) {
        this.enableCaptcha = enableCaptcha;
    }

    public boolean isSessionAuth() {
        return sessionAuth;
    }

    public void setSessionAuth(boolean sessionAuth) {
        this.sessionAuth = sessionAuth;
    }

    public void setFileUploadUrls(Set<String> fileUploadUrls) {
        if (fileUploadUrls != null) {
            this.fileUploadUrls.addAll(fileUploadUrls);
        }
    }

    public Set<String> getFileUploadUrls() {
        return fileUploadUrls;
    }
}
