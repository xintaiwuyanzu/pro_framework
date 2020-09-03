package com.dr.framework.task;

import com.dr.framework.core.security.SecurityHolder;
import org.springframework.core.task.TaskDecorator;

/**
 * @author dr
 */
public class SecurityHolderTaskDecorator implements TaskDecorator {
    @Override
    public Runnable decorate(Runnable runnable) {
        return new SecurityHolderRunable(runnable, SecurityHolder.get());
    }
}
