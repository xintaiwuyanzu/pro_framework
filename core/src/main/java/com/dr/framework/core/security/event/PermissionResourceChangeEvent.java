package com.dr.framework.core.security.event;

/**
 * 权限资源增删改查事件，用来清空缓存
 *
 * @author dr
 */
public class PermissionResourceChangeEvent extends SecurityEvent<String> {
    public PermissionResourceChangeEvent(String source) {
        super(source);
    }
}
