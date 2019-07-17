package com.dr.framework.core.orm.annotations;

import com.dr.framework.core.orm.module.Module;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标识一张表
 *
 * @author dr
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {
    String name() default "";

    String comment() default "";

    String simple() default "";

    String module() default Module.DEFAULT_MODULE;

    /**
     * @return true 表示表，false表示视图
     */
    boolean isTable() default true;

    /**
     * 当对应的表是视图的时候，需要手动声明创建语句
     *
     * @return
     */
    String createSql() default "";

}
