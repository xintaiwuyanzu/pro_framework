package com.dr.framework.core.orm.sql;

import org.apache.ibatis.type.JdbcType;
import org.springframework.util.StringUtils;

/**
 * 表示数据库列，用来构建数据查询功能
 *
 * @author dr
 * @see com.dr.framework.core.orm.sql.support.SqlQuery
 */
public class Column {
    public static Column function(Column column, String function, String alias) {
        Column column1 = new Column(column.getTable(), column.getName(), column.getAlias(), column.getJdbcType());
        column1.function = function;
        if (!StringUtils.isEmpty(alias)) {
            column1.alias = alias;
        }
        return column1;
    }

    public static Column function(Column column, String function) {
        return function(column, function, null);
    }

    public static Column distinct(Column column, String alias) {
        return function(column, "distinct", alias);
    }

    public static Column distinct(Column column) {
        return distinct(column, null);
    }

    public static Column count(Column column, String alias) {
        return function(column, "count", alias);
    }

    public static Column count(Column column) {
        return count(column, null);
    }

    public static Column sum(Column column, String alias) {
        return function(column, "sum", alias);
    }

    public static Column sum(Column column) {
        return sum(column, null);
    }

    public static Column avg(Column column, String alias) {
        return function(column, "avg", alias);
    }

    public static Column avg(Column column) {
        return avg(column, null);
    }

    public static Column max(Column column, String alias) {
        return function(column, "max", alias);
    }

    public static Column max(Column column) {
        return max(column, null);
    }

    public static Column min(Column column, String alias) {
        return function(column, "min", alias);
    }

    public static Column min(Column column) {
        return min(column, null);
    }

    /**
     * 表名
     */
    private String table;
    /**
     * 列名
     */
    private String name;
    /**
     * 别名，一般用作映射do的属性
     */
    private String alias;
    /**
     * 数据库函数
     */
    private String function;
    /**
     * 数据库类型
     *
     * @deprecated 这个属性没用了，计划删除掉
     */
    @Deprecated
    private JdbcType jdbcType;

    public Column(String table, String name, String alias) {
        this.table = table;
        this.name = name;
        this.alias = alias;
    }

    public Column(String table, String name, String alias, JdbcType type) {
        this.table = table;
        this.name = name;
        this.alias = alias;
        this.jdbcType = type;
    }

    public Column alias(String alias) {
        if (StringUtils.isEmpty(alias)) {
            return this;
        } else {
            Column column = new Column(getTable(), getName(), alias, getJdbcType());
            column.function = function;
            return column;
        }
    }

    public Column function(String function, String alias) {
        Column column1 = new Column(this.table, this.name, this.alias, getJdbcType());
        column1.function = function;
        if (!StringUtils.isEmpty(alias)) {
            column1.alias = alias;
        }
        return column1;
    }

    public Column function(String function) {
        return function(function, null);
    }

    public Column distinct(String alias) {
        return function("distinct", alias);
    }

    public Column distinct() {
        return distinct("");
    }

    public Column count(String alias) {
        return function("count", alias);
    }

    public Column count() {
        return count("");
    }

    public Column sum(String alias) {
        return function("sum", alias);
    }

    public Column sum() {
        return sum("");
    }

    public Column avg(String alias) {
        return function("avg", alias);
    }

    public Column avg() {
        return avg("");
    }

    public Column max(String alias) {
        return function("max", alias);
    }

    public Column max() {
        return max("");
    }

    public Column min(String alias) {
        return function("min", alias);
    }

    public Column min() {
        return min("");
    }

    public String getTable() {
        return table.trim();
    }

    public String getName() {
        return name;
    }

    public String getAlias() {
        return alias;
    }

    protected void setAlias(String alias) {
        this.alias = alias;
    }

    public String getFunction() {
        return function;
    }

    public JdbcType getJdbcType() {
        return jdbcType;
    }
}
