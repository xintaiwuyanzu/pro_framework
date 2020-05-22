package com.dr.framework.common.config.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注解实体类，表明该类是
 * {@link com.dr.framework.common.config.entity.CommonConfig}
 * 的映射
 *
 * @author dr
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Config {
    /**
     * 类型
     *
     * @return
     */
    String value() default "";

    /**
     * 类型
     * 与{@link #value()}含义相同，填任何一个都可以
     *
     * @return
     */
    String type() default "";

    /**
     * 编码
     *
     * @return
     */
    String code() default "default";

}
