package com.dr.framework.core.orm.database;

/**
 * 大小写敏感类型
 *
 * @author dr
 */
public enum CaseType {
    /**
     * 自动处理，根据Dialect默认配置实现
     */
    AUTO,
    /**
     * 大小写不敏感，但是都转换为大写
     */
    UPPER_CASE,
    /**
     * 大小写不敏感，但是都转换为小写
     */
    LOWER_CASE,
    /**
     * 大小写敏感，定义成啥样就转换成啥样
     */
    NO_CASE;
}
