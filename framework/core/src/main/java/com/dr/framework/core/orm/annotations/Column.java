package com.dr.framework.core.orm.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.sql.Types;

/**
 * 标识一张表字段
 *
 * @author dr
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {
    /**
     * 字段名称
     *
     * @return
     */
    String name() default "";

    /**
     * 字段备注
     *
     * @return
     */
    String comment() default "";

    /**
     * 业务显示名称
     *
     * @return
     */
    String simple() default "";

    /**
     * 字段排序
     *
     * @return
     */
    int order() default 0;

    /**
     * 可否为空
     *
     * @return
     */
    boolean nullable() default true;

    /**
     * 是否可插入
     *
     * @return
     */
    boolean insertable() default true;

    /**
     * 是否可更新
     *
     * @return
     */
    boolean updatable() default true;

    /**
     * 字段长度
     *
     * @return
     */
    int length() default 255;

    /**
     * 精度，数字类型时总共有几位
     *
     * @return
     * @deprecated 可以使用 {@link #length} 替代，将来计划去掉此属性
     */
    @Deprecated
    int precision() default 0;

    /**
     * 刻度，数字类型时小数点后有几位
     *
     * @return
     */
    int scale() default 0;

    /**
     * 数据类型
     * 如果没有声明，程序会根据字段类型自动判断类型
     *
     * @return
     */
    ColumnType type() default ColumnType.AUTO;

    /**
     * 直接指定声明数据库类型，代码生成的时候可以使用
     *
     * @return
     */
    int jdbcType() default Types.NULL;

    /**
     * TODO 现在不处理外键信息
     * 外键链接信息
     *
     * @return
     */
    Join[] joins() default {};

    @interface Join {
        /**
         * 与哪张表链接
         *
         * @return
         */
        Class<?> table();

        /**
         * join字段
         *
         * @return
         */
        String column();
    }
}
