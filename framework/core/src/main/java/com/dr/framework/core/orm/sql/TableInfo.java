package com.dr.framework.core.orm.sql;

import java.util.List;

/**
 * @author dr
 */
public interface TableInfo {

    /**
     * 获取所属模块
     *
     * @return
     */
    @Deprecated
    public String moudle();

    /**
     * 获取一张表的表名
     *
     * @return
     * @deprecated
     */
    @Deprecated
    public String table();

    /**
     * 获取主建列名
     *
     * @return
     */
    @Deprecated
    public Column pk();

    /**
     * 获取一张表的所有列
     *
     * @return
     */
    public List<Column> columns();

}
