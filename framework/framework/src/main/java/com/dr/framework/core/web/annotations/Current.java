package com.dr.framework.core.web.annotations;

import com.dr.framework.core.organise.entity.Organise;
import com.dr.framework.core.organise.entity.Person;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 能够获取当前相关信息的类
 * <p>
 * 当前登录人员相关注解依赖
 * 目前只支持
 * 当前登陆人员{@link Person }
 * 当前登录人员所属机构{@link Organise}
 *
 * @author dr
 * @see com.dr.framework.core.web.resolver.CurrentParamResolver
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Current {
    /**
     * 是否必须，如果必填说明访问该方法需要登录
     *
     * @return
     */
    boolean required() default false;
}
