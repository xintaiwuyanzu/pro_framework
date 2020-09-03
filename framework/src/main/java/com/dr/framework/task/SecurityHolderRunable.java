package com.dr.framework.task;

import com.dr.framework.core.security.SecurityHolder;

/**
 * 异步线程设置当前线程当前登录人员环境变量
 *
 * @author dr
 */
class SecurityHolderRunable implements Runnable {
    private Runnable delegate;
    private SecurityHolder securityHolder;

    public SecurityHolderRunable(Runnable delegate, SecurityHolder securityHolder) {
        this.delegate = delegate;
        this.securityHolder = securityHolder;
    }

    @Override
    public void run() {
        if (securityHolder != null) {
            SecurityHolder.set(securityHolder);
        }
        try {
            delegate.run();
        } finally {
            SecurityHolder.reset();
        }
    }
}
