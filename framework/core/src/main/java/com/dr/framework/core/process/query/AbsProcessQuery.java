package com.dr.framework.core.process.query;

/**
 * 简单抽象父类
 *
 * @author dr
 */
public class AbsProcessQuery<T extends AbsProcessQuery> {
    /**
     * 是否查询定义的扩展属性
     */
    boolean withProperty = false;

    public T withProperty() {
        this.withProperty = true;
        return (T) this;
    }

    public boolean isWithProperty() {
        return withProperty;
    }
}
