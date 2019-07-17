package com.dr.framework.core.orm.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义mapper扩展注解，可以指定适配某些实体类
 *
 * @author dr
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Mapper {
    /**
     * 支持操作哪些实体类
     *
     * @return
     */
    Class[] value() default {};

    /**
     * 支持哪些模块
     *
     * @return
     */
    String[] module() default {};
}
