package com.dr.framework.core.security;

import com.dr.framework.core.security.bo.ClientInfo;

final class SecurityHolder {
    private static final ThreadLocal<ClientInfo> contextHolder = new ThreadLocal<>();

    static ClientInfo get() {
        return contextHolder.get();
    }
}
