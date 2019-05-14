package com.dr.framework.core.orm.sql;

import java.util.List;

/**
 * @author dr
 */
public interface TableInfo extends Relation {

    /**
     * 获取所属模块
     *
     * @return
     */
    public String moudle();

    /**
     * 方法名称规范了
     *
     * @return
     */
    @Override
    default String getName() {
        return table();
    }

    /**
     * 获取一张表的表名
     *
     * @return
     * @see #getName()
     * @deprecated
     */
    @Deprecated
    public String table();

    /**
     * 获取主建列名
     *
     * @return
     */
    public Column pk();

    /**
     * 获取一张表的所有列
     *
     * @return
     */
    public List<Column> columns();

}
