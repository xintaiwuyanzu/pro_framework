package com.dr.framework.core.orm.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表示一个列的索引
 *
 * @author dr
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Index {
    /**
     * name的别名
     *
     * @return
     */
    String value() default "";

    /**
     * 索引名称
     * 没有的话默认使用列名称,
     * 如果两个列索引的名称是相同的话，则创建联合索引
     *
     * @return
     */
    String name() default "";

    /**
     * 是否唯一
     *
     * @return
     */
    boolean unique() default false;

    /**
     * 索引类型
     *
     * @return
     */
    int type() default -1;

    /**
     * 联合主键的时候用到了，用来排序使用
     *
     * @return
     */
    int order() default 0;

    /**
     * 排序规则
     *
     * @return
     */
    ASCOrDESC asc() default ASCOrDESC.ASC;

    enum ASCOrDESC {
        ASC,
        DESC,
        NULL
    }
}
