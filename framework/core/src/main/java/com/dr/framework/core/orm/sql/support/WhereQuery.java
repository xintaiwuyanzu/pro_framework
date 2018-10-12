package com.dr.framework.core.orm.sql.support;

import com.dr.framework.core.orm.sql.Column;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * where SQL语句
 * TODO having
 */
class WhereQuery extends AbstractSqlQuery {
    static final String QUERYPATAM = "$QP";
    List<Column> groupBys = new ArrayList<>();
    List<OrderBy> orderBys = new ArrayList<>();
    AtomicInteger atomicInteger = new AtomicInteger();
    WhereSqlConcats whereSqls = new WhereSqlConcats();

    void groupBy(Column... columns) {
        if (columns != null) {
            groupBys.addAll(Arrays.asList(columns));
        }
    }

    void orderBy(Column... columns) {
        if (columns != null) {
            for (Column column : columns) {
                orderBys.add(new OrderBy(column));
            }
        }
    }

    void orderByDesc(Column... columns) {
        if (columns != null) {
            for (Column column : columns) {
                orderBys.add(new OrderBy(column, true));
            }
        }
    }

    void concatSql(Column column, String prefix, String suffix) {
        whereSqls.concat(AND_, new ConcatSql(column, prefix, suffix));
    }

    void concatSqlWithData(Column column, String prefix, String suffix, Serializable... data) {
        if (data != null && data.length > 0) {
            Serializable parsedData;
            if (data.length == 1) {
                parsedData = data[0];
            } else {
                parsedData = Arrays.asList(data).stream().map(d -> d.toString()).collect(Collectors.joining(","));
            }
            whereSqls.concat(AND_, new ConcatTestSqlWithData(column, prefix, suffix, parsedData));
        }
    }

    void concatTestSql(Column column, String prefix, String suffix) {
        whereSqls.concat(AND_, new ConcatTestSql(column, prefix, suffix));
    }

    void like(Column column, boolean isLike, boolean pre, boolean end, Serializable data) {
        if (!StringUtils.isEmpty(data)) {
            whereSqls.concat(AND_, new LikeSqlWithData(column, isLike, pre, end, data));
        }
    }

    void like(Column column, boolean isLike, boolean pre, boolean end) {
        whereSqls.concat(AND_, new LikeTestSql(column, isLike, pre, end));
    }

    void add(String part) {
        whereSqls.concat(part, null);
    }

    String getColumnKey() {
        return QUERYPATAM + atomicInteger.incrementAndGet();
    }

    @Override
    String sql(TableAlias tableAlias, SqlQuery sqlQuery) {
        return sql(tableAlias, sqlQuery, true);
    }

    String sql(TableAlias tableAlias, SqlQuery sqlQuery, boolean includeOrderBy) {
        StringBuilder builder = new StringBuilder();
        MetaObject metaobject = SystemMetaObject.forObject(sqlQuery);
        parseWhere(builder, metaobject, tableAlias, sqlQuery);
        if (includeOrderBy) {
            sqlClause(builder, " GROUP BY ", groupByParts(tableAlias), "", "", ", ");
            sqlClause(builder, " ORDER BY ", orderByParts(tableAlias), "", "", ", ");
        }
        return builder.toString();
    }

    boolean hasWhere(TableAlias tableAlias, SqlQuery sqlQuery) {
        StringBuilder builder = new StringBuilder();
        MetaObject metaobject = SystemMetaObject.forObject(sqlQuery);
        parseWhere(builder, metaobject, tableAlias, sqlQuery);
        return builder.toString().length() != 0;
    }

    private void parseWhere(StringBuilder builder, MetaObject metaobject, TableAlias tableAlias, SqlQuery sqlQuery) {
        String whereSql = whereSqls.sql(metaobject, tableAlias, sqlQuery);
        if (!StringUtils.isEmpty(whereSql)) {
            builder.append(" where \n ((");
            builder.append(whereSql);
            builder.append("))");
        }
    }

    private List<String> orderByParts(TableAlias tableAlias) {
        return orderBys.stream().map(orderBy -> {
            StringBuilder sb = new StringBuilder();
            if (!StringUtils.isEmpty(orderBy.column.getTable())) {
                sb.append(tableAlias.alias(orderBy.column.getTable()));
                sb.append(".");
            }
            sb.append(orderBy.column.getName());
            if (orderBy.desc) {
                sb.append(" desc");
            }
            return sb.toString();
        }).collect(Collectors.toList());
    }

    private List<String> groupByParts(TableAlias tableAlias) {
        return groupBys.stream().map(column -> {
            StringBuilder sb = new StringBuilder();
            if (!StringUtils.isEmpty(column.getTable())) {
                sb.append(tableAlias.alias(column.getTable()));
                sb.append(".");
            }
            sb.append(column.getName());
            return sb.toString();
        }).collect(Collectors.toList());

    }

    class WhereSqlConcats {
        WhereSql sql;
        String concat;
        WhereSqlConcats next;
        boolean sqlTested = false;

        boolean hasComplex() {
            boolean h = false;
            if (sqlTested) {
                h = concat.equalsIgnoreCase(AND_NEW) || concat.equalsIgnoreCase(OR_NEW);
            }
            if (next != null) {
                h = h || next.hasComplex();
            }
            return h;
        }

        void concat(String concat, WhereSql whereSql) {
            if (StringUtils.isEmpty(this.concat)) {
                this.concat = concat;
                this.sql = whereSql;
            } else {
                if (next == null) {
                    next = new WhereSqlConcats();
                    next.concat = concat;
                    next.sql = whereSql;
                } else {
                    next.concat(concat, whereSql);
                }
            }
        }

        String sql(MetaObject metaObject, TableAlias alias, SqlQuery sqlQuery) {
            StringBuilder builder = new StringBuilder();
            if (sql != null) {
                if (sql.test(metaObject)) {
                    sqlTested = true;
                    String sqlStr = sql.getSql(alias, sqlQuery);
                    if (!StringUtils.isEmpty(sqlStr)) {
                        builder.append(sqlStr);
                    }
                }
            }
            if (next != null) {
                String sql = next.sql(metaObject, alias, sqlQuery);
                if (!StringUtils.isEmpty(sql)) {
                    boolean canCancat = (builder.length() > 0 || !concat.equalsIgnoreCase(AND_)) && canCancat(sql);
                    if (canCancat) {
                        builder.append(concat);
                    }
                    builder.append(sql);
                }
            }
            return builder.toString();
        }

        private boolean canCancat(String sql) {
            for (String concat : concats) {
                if (sql.startsWith(concat)) {
                    return false;
                }
            }
            return true;
        }
    }

    abstract class WhereSql {
        StringBuilder formatColumn(Column column, TableAlias tableAlias) {
            StringBuilder builder = new StringBuilder();
            if (!StringUtils.isEmpty(column.getTable())) {
                builder.append(tableAlias.alias(column.getTable()));
                builder.append(".");
            }
            builder.append(column.getName());
            builder.append(" ");
            return builder;
        }

        StringBuilder queryParam(SqlQuery sqlQuery) {
            StringBuilder stringBuilder = new StringBuilder("#{");
            if (sqlQuery.containsKey((SqlQuery.QUERY_PARAM))) {
                Object param = sqlQuery.get(SqlQuery.QUERY_PARAM);
                if (!StringUtils.isEmpty(param)) {
                    stringBuilder.append(param);
                    stringBuilder.append(".");
                }
            }
            return stringBuilder;
        }

        StringBuilder columnKey(SqlQuery sqlQuery, String key) {
            return queryParam(sqlQuery).append(key).append("}");
        }

        abstract boolean test(MetaObject metaobject);

        abstract String getSql(TableAlias alias, SqlQuery sqlQuery);
    }

    abstract class TrueSql extends WhereSql {
        @Override
        boolean test(MetaObject metaobject) {
            return true;
        }
    }

    class ConcatSql extends TrueSql {
        Column column;
        String preffix;
        String suffix;

        ConcatSql(Column column, String preffix, String suffix) {
            this.column = column;
            this.preffix = preffix;
            this.suffix = suffix;
        }

        @Override
        public String getSql(TableAlias alias, SqlQuery sqlQuery) {
            String sql = "";
            if (sqlQuery.containsKey(SqlQuery.ENTITY_KEY)) {
                sql = formatColumn(column, alias).append(preffix).append(columnKey(column, sqlQuery)).append(suffix).toString();
            }
            return sql;
        }


        private StringBuilder columnKey(Column column, SqlQuery sqlQuery) {
            return queryParam(sqlQuery).append(SqlQuery.ENTITY_KEY)
                    .append(".")
                    .append(column.getAlias())
                    .append("}");
        }
    }

    class ConcatTestSql extends ConcatSql {

        ConcatTestSql(Column column, String preffix, String suffix) {
            super(column, preffix, suffix);
        }

        @Override
        public boolean test(MetaObject metaobject) {
            Object object = metaobject.getValue(SqlQuery.ENTITY_KEY);
            if (object != null) {
                return !StringUtils.isEmpty(metaobject.getValue(SqlQuery.ENTITY_KEY + "." + column.getAlias()));
            } else {
                return false;
            }
        }
    }

    class ConcatTestSqlWithData extends WhereSql {
        Column column;
        String preffix;
        String suffix;
        Object data;

        ConcatTestSqlWithData(Column column, String preffix, String suffix, Object data) {
            this.column = column;
            this.preffix = preffix;
            this.suffix = suffix;
            this.data = data;
        }

        @Override
        boolean test(MetaObject metaobject) {
            return !StringUtils.isEmpty(data);
        }

        @Override
        String getSql(TableAlias alias, SqlQuery sqlQuery) {
            String key = getColumnKey();
            sqlQuery.put(key, data);
            return formatColumn(column, alias).append(preffix).append(columnKey(sqlQuery, key)).append(suffix).toString();
        }
    }

    class LikeSqlWithData extends TrueSql {
        Column column;
        boolean isLike;
        boolean pre;
        boolean end;
        Serializable data;

        LikeSqlWithData(Column column, boolean isLike, boolean pre, boolean end, Serializable data) {
            this.column = column;
            this.isLike = isLike;
            this.pre = pre;
            this.end = end;
            this.data = data;
        }

        @Override
        String getSql(TableAlias alias, SqlQuery sqlQuery) {
            String key = getColumnKey();
            StringBuilder dataBuilder = new StringBuilder();
            if (pre) {
                dataBuilder.append("%");
            }
            dataBuilder.append(data);
            if (end) {
                dataBuilder.append("%");
            }
            sqlQuery.put(key, dataBuilder.toString());
            StringBuilder sqlBuilder = formatColumn(column, alias);
            if (isLike) {
                sqlBuilder.append(" like ");
            } else {
                sqlBuilder.append(" not like ");
            }
            sqlBuilder.append(columnKey(sqlQuery, key));
            return sqlBuilder.toString();
        }
    }

    class LikeTestSql extends LikeSqlWithData {
        LikeTestSql(Column column, boolean isLike, boolean pre, boolean end) {
            super(column, isLike, pre, end, null);
        }

        @Override
        boolean test(MetaObject metaobject) {
            Object object = metaobject.getValue(SqlQuery.ENTITY_KEY);
            if (object != null) {
                data = (Serializable) metaobject.getValue(SqlQuery.ENTITY_KEY + "." + column.getAlias());
                return !StringUtils.isEmpty(data);
            } else {
                return false;
            }
        }
    }

    private static class OrderBy {
        boolean desc = false;
        Column column;

        OrderBy(Column column) {
            this.column = column;
        }

        OrderBy(Column column, boolean desc) {
            this.column = column;
            this.desc = desc;
        }
    }
}
