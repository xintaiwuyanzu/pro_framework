package com.dr.framework.core.orm.sql.support;

import com.dr.framework.core.orm.jdbc.Relation;
import com.dr.framework.core.orm.module.EntityRelation;
import com.dr.framework.core.orm.sql.Column;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

class FromQuery extends AbstractSqlQuery {
    JoinColumns join = new JoinColumns();
    JoinColumns innerJoin = new JoinColumns();
    JoinColumns outerJoin = new JoinColumns();
    JoinColumns leftOuterJoin = new JoinColumns();
    JoinColumns rightOuterJoin = new JoinColumns();
    //TODO 嵌套子查询别名有问题
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
        boolean joined = false;
        if (left.getTable().equalsIgnoreCase(table) || right.getTable().equalsIgnoreCase(table)) {
            joined = true;
            String tableName;
            if (left.getTable().equalsIgnoreCase(table)) {
                joinColumns.add(left, right);
                tableName = right.getTable().toUpperCase();
            } else {
                joinColumns.add(right, left);
                tableName = left.getTable().toUpperCase();
            }
            if (!tableAlias.alias.containsKey(tableName)) {
                alias(tableName, null);
            }
        } else {
            String leftTableName = left.getTable().toUpperCase();
            String rightTableName = right.getTable().toUpperCase();
            if (joinColumns.columnMap.containsKey(leftTableName)) {
                joined = true;
                joinColumns.add(left, right, 1);
                if (!tableAlias.alias.containsKey(rightTableName)) {
                    alias(rightTableName, null);
                }
            } else if (joinColumns.columnMap.containsKey(rightTableName)) {
                joined = true;
                joinColumns.add(right, left, 1);
                if (!tableAlias.alias.containsKey(leftTableName)) {
                    alias(leftTableName, null);
                }
            }
        }
        Assert.isTrue(joined, "请使用相关的表执行join操作");
    }

    void alias(String table, String alias) {
        if (StringUtils.hasText(table)) {
            if (StringUtils.hasText(alias)) {
                Assert.isTrue(!tableAlias.alias.containsValue(alias), "不能设置重复的表别名【" + alias + "】");
            }
            tableAlias.alias.put(table.toUpperCase(), alias);
        }
    }

    private Map<Class, String> classStringMap = new HashMap<>(4);


    public void aliasClass(Class ec, String alias) {
        if (StringUtils.hasText(alias)) {
            classStringMap.put(ec, alias);
        }
    }

    public void bindRelation(Relation<? extends Column> relation) {
        if (relation instanceof EntityRelation) {
            //TODO
            //String alias = classStringMap.get(((EntityRelation) relation).getEntityClass());
            //alias(relation.getName(), alias);
        }
        table = relation.getName();
    }

    @Override
    String sql(TableAlias tableAlias, SqlQuery sqlQuery) {
        StringBuilder builder = new StringBuilder();
        sqlClause(builder, " from ", Collections.singletonList(convertTableName(sqlQuery) + " " + tableAlias.alias(table)), "", "", ", ");
        joins(tableAlias, builder);
        return builder.toString();
    }

    String convertTableName(SqlQuery sqlQuery) {
        return sqlQuery.getDialect().convertTableName(table);
    }

    String table(TableAlias tableAlias, SqlQuery sqlQuery) {
        StringBuilder builder = new StringBuilder();
        sqlClause(builder, "", Collections.singletonList(convertTableName(sqlQuery) + " " + tableAlias.alias(table)), "", "", ", ");
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
