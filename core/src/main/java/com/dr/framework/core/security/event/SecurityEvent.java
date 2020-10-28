package com.dr.framework.core.security.event;

import com.dr.framework.core.event.BaseEvent;

/**
 * 权限相关事件
 *
 * @param <T>
 * @author dr
 */
public class SecurityEvent<T> extends BaseEvent<T> {
    public SecurityEvent(T source) {
        super(source);
    }
}
