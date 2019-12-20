package com.dr.framework.core.orm.sql.support;

import com.dr.framework.core.orm.sql.Column;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.*;
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
                orderBys.add(new ColumnOrderBy(column, false));
            }
        }
    }

    void orderByDesc(Column... columns) {
        if (columns != null) {
            for (Column column : columns) {
                orderBys.add(new ColumnOrderBy(column, true));
            }
        }
    }

    void orderBy(String alias, boolean desc) {
        if (!StringUtils.isEmpty(alias)) {
            orderBys.add(new AliasOrderBy(alias, desc));
        }
    }

    void concatSql(Column column, String prefix, String suffix) {
        whereSqls.concat(AND_, new ConcatSql(column, prefix, suffix));
    }

    void concatSqlWithQuery(Column column, String prefix, String suffix, SqlQuery query) {
        whereSqls.concat(AND_, new ConcatTestSqlWithQuery(column, prefix, suffix, query));
    }

    void concatSqlWithData(Column column, String prefix, String suffix, Serializable... data) {
        if (data != null && data.length > 0) {
            Serializable parsedData;
            if (data.length == 1) {
                parsedData = data[0];
            } else {
                parsedData = Arrays.asList(data).stream().map(Objects::toString).collect(Collectors.joining(","));
            }
            whereSqls.concat(AND_, new ConcatTestSqlWithData(column, prefix, suffix, parsedData));
        }
    }

    void concatTestSql(Column column, String prefix, String suffix) {
        whereSqls.concat(AND_, new ConcatTestSql(column, prefix, suffix));
    }

    void collectionSql(Column column, String prefix, String suffix, Collection<? extends Serializable> collection) {
        if (collection != null && !collection.isEmpty()) {
            whereSqls.concat(AND_, new CollectionSql(column, prefix, suffix, collection));
        }
    }

    void pureSql(Column column, String sql) {
        whereSqls.concat(AND_, new PureSql(column, sql));
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

    void between(Column column, Comparable start, Comparable end) {
        whereSqls.concat(AND_, new Between(column, start, end));
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
        return orderBys.stream().map(orderBy -> orderBy.sql(tableAlias)).collect(Collectors.toList());
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
                String nextSql = next.sql(metaObject, alias, sqlQuery);
                if (!StringUtils.isEmpty(nextSql)) {
                    boolean canCancat = (builder.length() > 0 || !concat.equalsIgnoreCase(AND_)) && canCancat(nextSql);
                    if (canCancat) {
                        builder.append(concat);
                    }
                    builder.append(nextSql);
                }
            }
            return builder.toString();
        }

        private boolean canCancat(String sql) {
            for (String cs : concats) {
                if (sql.startsWith(cs)) {
                    return false;
                }
            }
            return true;
        }
    }

    abstract class WhereSql {
        StringBuilder formatColumn(Column column, TableAlias tableAlias) {
            return new StringBuilder(AbstractSqlQuery.formatSql(column, tableAlias, false));
        }

        StringBuilder queryParam(SqlQuery sqlQuery) {
            return queryParam(sqlQuery, "#");
        }

        StringBuilder queryParam(SqlQuery sqlQuery, String placeHolder) {
            if (sqlQuery.getParent() != null) {
                StringBuilder sb = queryParam(sqlQuery.getParent(), placeHolder);
                return sb.append(sqlQuery.getMapKey()).append('.');
            } else {
                StringBuilder stringBuilder = new StringBuilder(placeHolder)
                        .append("{");
                if (sqlQuery.containsKey((SqlQuery.QUERY_PARAM))) {
                    Object param = sqlQuery.get(SqlQuery.QUERY_PARAM);
                    if (!StringUtils.isEmpty(param)) {
                        stringBuilder.append(param);
                        stringBuilder.append(".");
                    }
                }
                return stringBuilder;
            }
        }

        StringBuilder columnKey(SqlQuery sqlQuery, String key) {
            return queryParam(sqlQuery).append(key).append("}");
        }

        StringBuilder columnKey(SqlQuery sqlQuery, String key, String placeHolder) {
            return queryParam(sqlQuery, placeHolder).append(key).append("}");
        }

        StringBuilder columnKey(SqlQuery sqlQuery, String key, String placeHolder, String suffix) {
            return queryParam(sqlQuery, placeHolder).append(key).append(suffix).append("}");
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

    class CollectionSql extends TrueSql {
        Column column;
        String prefix;
        String suffix;
        Collection<? extends Serializable> collection;

        public CollectionSql(Column column, String prefix, String suffix, Collection<? extends Serializable> collection) {
            this.column = column;
            this.prefix = prefix;
            this.suffix = suffix;
            this.collection = collection;
        }

        @Override
        String getSql(TableAlias alias, SqlQuery sqlQuery) {
            String sql = "";
            if (sqlQuery.containsKey(SqlQuery.ENTITY_KEY)) {
                StringBuilder sb = formatColumn(column, alias)
                        .append(prefix);
                sb.append(collection.
                        stream()
                        .map(c -> {
                            //TODO 这里应该有问题
                            return ((Serializable) c).toString();
                        })
                        .collect(Collectors.joining(","))
                );
                sql = sb.append(suffix)
                        .toString();
            }
            return sql;
        }
    }


    class NoParamSql extends TrueSql {

        Column left;
        Column right;

        @Override
        String getSql(TableAlias alias, SqlQuery sqlQuery) {
            return null;
        }
    }

    class PureSql extends WhereSql {
        Column column;
        String sql;

        PureSql(Column column, String sql) {
            this.column = column;
            this.sql = sql;
        }

        @Override
        boolean test(MetaObject metaobject) {
            return !StringUtils.isEmpty(sql);
        }

        @Override
        String getSql(TableAlias alias, SqlQuery sqlQuery) {
            return formatColumn(column, alias).append(' ').append(sql).toString();
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
        String getSql(TableAlias alias, SqlQuery sqlQuery) {
            String sql = "";
            if (sqlQuery.containsKey(SqlQuery.ENTITY_KEY)) {
                sql = formatColumn(column, alias)
                        .append(' ')
                        .append(preffix)
                        .append(columnKey(column, sqlQuery))
                        .append(suffix).toString();
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
        boolean test(MetaObject metaobject) {
            Object object = metaobject.getValue(SqlQuery.ENTITY_KEY);
            if (object != null) {
                return !StringUtils.isEmpty(metaobject.getValue(SqlQuery.ENTITY_KEY + "." + column.getAlias()));
            } else {
                return false;
            }
        }
    }

    class ConcatTestSqlWithQuery extends WhereSql {
        Column column;
        String preffix;
        String suffix;
        SqlQuery data;

        ConcatTestSqlWithQuery(Column column, String preffix, String suffix, SqlQuery data) {
            this.column = column;
            this.preffix = preffix;
            this.suffix = suffix;
            this.data = data;
        }

        @Override
        boolean test(MetaObject metaobject) {
            boolean tested = data.columnSize() == 1;
            if (!tested) {
                SqlQuery.logger.warn("子查询语句返回列只能为一行");
            }
            return tested;
        }

        @Override
        String getSql(TableAlias alias, SqlQuery sqlQuery) {
            String key = getColumnKey();
            data.setParent(sqlQuery, key);
            sqlQuery.put(key, data);
            return formatColumn(column, alias)
                    .append(' ')
                    .append(preffix)
                    .append("(select ")
                    .append(data.get("$columns"))
                    .append(' ')
                    .append(data.get("$from"))
                    .append(' ')
                    .append(data.get("$whereno"))
                    .append(") ")
                    .append(suffix).toString();
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
            return formatColumn(column, alias)
                    .append(' ')
                    .append(preffix)
                    .append(columnKey(sqlQuery, key))
                    .append(suffix).toString();
        }
    }

    class Between extends WhereSql {
        Column column;
        Comparable start,
                end;

        Between(Column column, Comparable start, Comparable end) {
            this.column = column;
            this.start = start;
            this.end = end;
        }

        @Override
        boolean test(MetaObject metaobject) {
            return !(StringUtils.isEmpty(start) || StringUtils.isEmpty(end)) && start.compareTo(end) <= 0;
        }

        @Override
        String getSql(TableAlias alias, SqlQuery sqlQuery) {
            String statKey = getColumnKey();
            String endKey = getColumnKey();
            StringBuilder sqlBuilder = formatColumn(column, alias);
            sqlBuilder.append(" between ");
            sqlQuery.put(statKey, start);
            sqlBuilder.append(columnKey(sqlQuery, statKey));
            sqlBuilder.append(" and ");
            sqlQuery.put(endKey, end);
            sqlBuilder.append(columnKey(sqlQuery, endKey));
            return sqlBuilder.toString();
        }
    }

    public static Serializable cleanXSS(Serializable serializable) {
        if (serializable instanceof String) {
            String value = (String) serializable;
            //You'll need to remove the spaces from the html entities below
            value = value.replaceAll("<", "& lt;").replaceAll(">", "& gt;");
            value = value.replaceAll("\\(", "& #40;").replaceAll("\\)", "& #41;");
            value = value.replaceAll("'", "& #39;");
            value = value.replaceAll("eval\\((.*)\\)", "");
            value = value.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']", "\"\"");
            value = value.replaceAll("script", "");
            value = value.replaceAll("[*]", "[" + "*]");
            value = value.replaceAll("[+]", "[" + "+]");
            value = value.replaceAll("[?]", "[" + "?]");

            // replace sql 这里可以自由发挥
            String[] values = value.split(" ");

            String badStr = "'|and|exec|execute|insert|select|delete|update|count|drop|%|chr|mid|master|truncate|" +
                    "char|declare|sitename|net user|xp_cmdshell|;|or|-|+|,|like'|and|exec|execute|insert|create|drop|" +
                    "table|from|grant|use|group_concat|column_name|" +
                    "information_schema.columns|table_schema|union|where|select|delete|update|order|by|count|" +
                    "chr|mid|master|truncate|char|declare|or|;|-|--|,|like|//|/|%|#";

            String[] badStrs = badStr.split("\\|");
            for (int i = 0; i < badStrs.length; i++) {
                for (int j = 0; j < values.length; j++) {
                    if (values[j].equalsIgnoreCase(badStrs[i])) {
                        values[j] = "forbid";
                    }
                }
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < values.length; i++) {
                if (i == values.length - 1) {
                    sb.append(values[i]);
                } else {
                    sb.append(values[i] + " ");
                }
            }
            value = sb.toString();
            return value;
        }
        return serializable;
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
            dataBuilder.append(cleanXSS(data));
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

    abstract class OrderBy {
        boolean desc = false;

        OrderBy(boolean desc) {
            this.desc = desc;
        }

        abstract String sql(TableAlias alias);
    }

    class ColumnOrderBy extends OrderBy {
        Column column;

        ColumnOrderBy(Column column, boolean desc) {
            super(desc);
            this.column = column;
        }

        @Override
        String sql(TableAlias alias) {
            StringBuilder sb = new StringBuilder();
            if (!StringUtils.isEmpty(column.getTable())) {
                sb.append(alias.alias(column.getTable()));
                sb.append(".");
            }
            sb.append(column.getName());
            if (desc) {
                sb.append(" desc");
            }
            return sb.toString();
        }
    }

    class AliasOrderBy extends OrderBy {
        String alias;

        AliasOrderBy(String alias, boolean desc) {
            super(desc);
            this.alias = alias;
        }

        @Override
        String sql(TableAlias tableAlias) {
            return desc ? alias + " desc" : alias;
        }
    }
}
