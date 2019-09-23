package com.dr.framework.core.system.entity;

import com.dr.framework.common.entity.BaseStatusEntity;
import com.dr.framework.core.orm.annotations.Column;
import com.dr.framework.core.orm.annotations.Table;
import com.dr.framework.core.util.Constants;

/**
 * 多系统
 *
 * @author dr
 */
@Table(name = SubSystem.SUBSYS_TABLE_NAME
        , module = Constants.SYS_MODULE_NAME
        , comment = "子系统基本信息"
        , genInfo = false)
public class SubSystem extends BaseStatusEntity<String> {
    /**
     * 默认系统id
     */
    public static final String DEFAULT_SYSTEM_ID = "default";
    public static final String SUBSYS_TABLE_NAME = Constants.SYS_TABLE_PREFIX + "sub_system";

    @Column(comment = "系统名称")
    private String sysName;
    @Column(comment = "系统简称")
    private String shortName;
    @Column(comment = "系统描述")
    private String sysDescription;
    @Column(comment = "ip地址")
    private String ip;
    @Column(comment = "访问端口")
    private Integer port;
    @Column(comment = "系统首页")
    private String homeAddress;
    @Column(comment = "登陆回调地址")
    private String callbackAddress;
    @Column(comment = "授权账号名称")
    private String loginName;
    @Column(comment = "授权账号密码")
    private String sysPassword;
    @Column(comment = "系统管理员名称")
    private String managerName;
    @Column(comment = "系统管理员电话")
    private String managerPhone;
    @Column(comment = "系统图标id")
    private String iconId;
    @Column(comment = "系统操作权限")
    private String permissionId;

    public String getSysName() {
        return sysName;
    }

    public void setSysName(String sysName) {
        this.sysName = sysName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getSysDescription() {
        return sysDescription;
    }

    public void setSysDescription(String sysDescription) {
        this.sysDescription = sysDescription;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(String homeAddress) {
        this.homeAddress = homeAddress;
    }

    public String getCallbackAddress() {
        return callbackAddress;
    }

    public void setCallbackAddress(String callbackAddress) {
        this.callbackAddress = callbackAddress;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getSysPassword() {
        return sysPassword;
    }

    public void setSysPassword(String sysPassword) {
        this.sysPassword = sysPassword;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public String getManagerPhone() {
        return managerPhone;
    }

    public void setManagerPhone(String managerPhone) {
        this.managerPhone = managerPhone;
    }

    public String getIconId() {
        return iconId;
    }

    public void setIconId(String iconId) {
        this.iconId = iconId;
    }

    public String getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(String permissionId) {
        this.permissionId = permissionId;
    }
}
