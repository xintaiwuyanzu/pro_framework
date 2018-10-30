package com.dr.framework.core.orm.sql.support;

import com.dr.framework.core.orm.sql.Column;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Arrays;

class FromQuery extends AbstractSqlQuery {
    JoinColumns join = new JoinColumns();
    JoinColumns innerJoin = new JoinColumns();
    JoinColumns outerJoin = new JoinColumns();
    JoinColumns leftOuterJoin = new JoinColumns();
    JoinColumns rightOuterJoin = new JoinColumns();
    TableAlias tableAlias = new TableAlias();
    String table;

    void from(String table, String alias) {
        this.table = table;
        alias(table, alias);
    }

    void join(Column left, Column right) {
        doJoin(join, left, right);
    }

    void innerJoin(Column left, Column right) {
        doJoin(innerJoin, left, right);
    }

    void leftOuterJoin(Column left, Column right) {
        doJoin(leftOuterJoin, left, right);
    }

    void rightOuterJoin(Column left, Column right) {
        doJoin(rightOuterJoin, left, right);
    }

    void outerJoin(Column left, Column right) {
        doJoin(outerJoin, left, right);
    }


    void doJoin(JoinColumns joinColumns, Column left, Column right) {
        Assert.isTrue(left.getTable().equalsIgnoreCase(table) || right.getTable().equalsIgnoreCase(table), "join 操作需有一列是【" + table + "】的列");
        String tableName;
        if (left.getTable().equalsIgnoreCase(table)) {
            joinColumns.add(left, right);
            tableName = right.getTable().toUpperCase();
        } else {
            joinColumns.add(right, left);
            tableName = left.getTable().toUpperCase();
        }
        if (!tableAlias.alias.containsKey(tableName)) {
            alias(right.getTable(), null);
        }
    }

    void alias(String table, String alias) {
        if (!StringUtils.isEmpty(table)) {
            if (!StringUtils.isEmpty(alias)) {
                Assert.isTrue(!tableAlias.alias.containsValue(alias), "不能设置重复的表别名【" + alias + "】");
            }
            tableAlias.alias.put(table.toUpperCase(), alias);
        }
    }

    @Override
    String sql(TableAlias tableAlias, SqlQuery sqlQuery) {
        StringBuilder builder = new StringBuilder();
        sqlClause(builder, " from ", Arrays.asList(table + " " + tableAlias.alias(table)), "", "", ", ");
        joins(tableAlias, builder);
        return builder.toString();
    }

    String table(TableAlias tableAlias, SqlQuery sqlQuery) {
        StringBuilder builder = new StringBuilder();
        sqlClause(builder, "", Arrays.asList(table + " " + tableAlias.alias(table)), "", "", ", ");
        joins(tableAlias, builder);
        return builder.toString();
    }


    private void joins(TableAlias tableAlias, StringBuilder builder) {
        sqlClause(builder, " JOIN ", join.parts(tableAlias), "", "", " JOIN ");
        sqlClause(builder, " INNER JOIN ", innerJoin.parts(tableAlias), "", "", " INNER JOIN ");
        sqlClause(builder, " OUTER JOIN ", outerJoin.parts(tableAlias), "", "", " OUTER JOIN ");
        sqlClause(builder, " LEFT OUTER JOIN ", leftOuterJoin.parts(tableAlias), "", "", " LEFT OUTER JOIN ");
        sqlClause(builder, " RIGHT OUTER JOIN ", rightOuterJoin.parts(tableAlias), "", "", " RIGHT OUTER JOIN ");
    }
}
