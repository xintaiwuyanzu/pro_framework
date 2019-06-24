package com.dr.framework.core.orm.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标识一个字段是主建
 *
 * @author dr
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Id {
    /**
     * 主键名称，与{@link #name()}同义
     *
     * @return 返回主键名称
     */
    String value() default "";

    /**
     * 主键名称，
     * 如果有联合主键，则设置多个主键名称相同即可
     *
     * @return 返回主键名称
     */
    String name() default "";

    /**
     * 是否自动生成主键
     *
     * @return 返回字段是否自动生成主键
     */
    boolean auto() default false;

    /**
     * 联合主键的时候用到了，用来排序使用
     *
     * @return 返回字段排序
     */
    int order() default 0;

    enum AutoGenerateType {
        /**
         * 如果列设置为自增，则使用列自增。
         * 如果
         */
        AUTO,
        /**
         * 自动创建uuid
         */
        UUID,
    }
}
