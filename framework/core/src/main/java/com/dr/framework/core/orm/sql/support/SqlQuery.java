package com.dr.framework.core.orm.sql.support;

import com.dr.framework.core.orm.sql.Column;
import com.dr.framework.core.orm.sql.TableInfo;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.dr.framework.core.orm.sql.support.AbstractSqlQuery.OR;

public final class SqlQuery<E> extends HashMap<String, Object> {
    public static final String COLUMNS = "${query#$columns}";
    public static final String FROM = "${query#$from}";
    public static final String TABLE = "${query#$table}";
    public static final String WHERE = "${query#$where}";
    public static final String WHERE_NO_ORERY_BY = "${query#$whereNO}";
    public static final String QUERY_CLASS_SUFFIX = "Info";
    public static final String QUERY_PARAM = "$QP";
    public static final String ENTITY_KEY = "ENTITY";

    private static Map<Class, Class<? extends TableInfo>> sqlQueryMap = new ConcurrentHashMap<>();

    /**
     * 根据model类获取该类的query帮助类
     *
     * @param modelClass
     * @param <T>
     * @return
     */
    public static <T extends TableInfo> T getTableInfo(Class modelClass) {
        if (!sqlQueryMap.containsKey(modelClass)) {
            loadTableInfo(modelClass);
        }
        if (sqlQueryMap.containsKey(modelClass)) {
            try {
                return (T) sqlQueryMap.get(modelClass).newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static void loadTableInfo(Class modelClass) {
        ClassLoader classLoader = modelClass.getClassLoader();
        String className = modelClass.getName() + QUERY_CLASS_SUFFIX;
        try {
            Class queryClass = classLoader.loadClass(className);
            sqlQueryMap.put(modelClass, queryClass);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static <T> SqlQuery<T> from(Class<T> entityClass) {
        return from(entityClass, null, true);
    }

    public static <T> SqlQuery<T> from(Class<T> entityClass, String alias) {
        return from(entityClass, alias, false);
    }

    public static <T> SqlQuery<T> from(Class<T> entityClass, boolean selectAllColumns) {
        return from(entityClass, null, selectAllColumns);
    }

    public static SqlQuery from(Class entityClass, String alias, boolean selectAllColumns) {
        SqlQuery sqlQuery = new SqlQuery();
        sqlQuery.entityClass = entityClass;
        TableInfo tableInfo = getTableInfo(entityClass);
        Assert.notNull(tableInfo, "未找到【" + entityClass.getName() + "】描述信息，请检查是否已生成代码！");
        sqlQuery.from(tableInfo.table(), alias);
        if (selectAllColumns) {
            List<Column> columns = tableInfo.columns();
            sqlQuery.column(columns.toArray(new Column[columns.size()]));
        }
        return sqlQuery;
    }

    public static SqlQuery from(Object entity) {
        return from(entity, null, false);
    }

    public static SqlQuery from(Object entity, boolean selectAllColumns) {
        return from(entity, null, selectAllColumns);
    }

    public static SqlQuery from(Object entity, String alia) {
        return from(entity, alia, false);
    }

    public static SqlQuery from(Object entity, String alias, boolean selectAllColumns) {
        SqlQuery sqlQuery = from(entity.getClass(), alias, selectAllColumns);
        if (entity != null) {
            sqlQuery.put(ENTITY_KEY, entity);
        }
        return sqlQuery;
    }

    /**
     * =================================================
     * 以上是静态方法，下面是实例方法和变量
     * =================================================
     */
    private WhereQuery whereQuery = new WhereQuery();
    private FromQuery fromQuery = new FromQuery();
    private ColumnsQuery columnsQuery = new ColumnsQuery();
    private Class<E> entityClass;

    @Override
    public boolean containsKey(Object key) {
        String keyStr = key.toString().trim();
        boolean su = super.containsKey(key);
        if (!su) {
            return keyStr.startsWith("$");
        } else {
            return true;
        }
    }

    /**
     * 判断是否有where语句
     *
     * @return
     */
    public boolean hasWhere() {
        return whereQuery.hasWhere(fromQuery.tableAlias, this);
    }

    @Override
    public Object get(Object key) {
        Object value = super.get(key);
        if (StringUtils.isEmpty(value)) {
            String keyStr = key.toString().trim().toLowerCase();
            if (StringUtils.isEmpty(value)) {
                switch (keyStr) {
                    case "$columns":
                        value = columnsQuery.sql(fromQuery.tableAlias, this);
                        break;
                    case "$from":
                        value = fromQuery.sql(fromQuery.tableAlias, this);
                        break;
                    case "$table":
                        value = fromQuery.table(fromQuery.tableAlias, this);
                        break;
                    case "$where":
                        value = whereQuery.sql(fromQuery.tableAlias, this, true);
                        break;
                    case "$whereno":
                        value = whereQuery.sql(fromQuery.tableAlias, this, false);
                        break;

                    default:
                        if (keyStr.startsWith("$alias")) {
                            String table = keyStr.replace("$alias", "");
                            value = fromQuery.tableAlias.alias(table.toUpperCase());
                        }
                        break;
                }
            }
        }
        return value;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(get("$columns").toString());
        stringBuilder.append(get("$from"));
        stringBuilder.append(get("$where"));
        return stringBuilder.toString();
    }

    /**
     * ==================================
     * column相关
     * ==================================
     */

    public SqlQuery alias(String table, String alias) {
        fromQuery.alias(table, alias);
        return this;
    }

    public SqlQuery column(Column... columns) {
        columnsQuery.column(columns);
        return this;
    }

    public SqlQuery exclude(Column... columns) {
        columnsQuery.exclude(columns);
        return this;
    }

    /**
     * =======================
     * from相关
     * =======================
     */
    private SqlQuery from(String table, String alias) {
        fromQuery.from(table, alias);
        return this;
    }

    public SqlQuery join(Column left, Column right) {
        fromQuery.join(left, right);
        return this;
    }

    public SqlQuery innerJoin(Column left, Column right) {
        fromQuery.innerJoin(left, right);
        return this;
    }

    public SqlQuery leftOuterJoin(Column left, Column right) {
        fromQuery.leftOuterJoin(left, right);
        return this;
    }

    public SqlQuery rightOuterJoin(Column left, Column right) {
        fromQuery.rightOuterJoin(left, right);
        return this;
    }

    public SqlQuery outerJoin(Column left, Column right) {
        fromQuery.outerJoin(left, right);
        return this;
    }


    /**
     * =======================
     * where相关
     * =======================
     */
    private SqlQuery concat(String prefix, String suffix, Column... columns) {
        if (columns != null) {
            for (Column column : columns) {
                whereQuery.concatSql(column, prefix, suffix);
            }
        }
        return this;
    }

    private SqlQuery concatTest(String prefix, String suffix, Column... columns) {
        if (columns != null) {
            for (Column column : columns) {
                whereQuery.concatTestSql(column, prefix, suffix);
            }
        }
        return this;
    }

    private SqlQuery concatWithData(Column column, String prefix, String suffix, Serializable... data) {
        whereQuery.concatSqlWithData(column, prefix, suffix, data);
        return this;
    }

    private SqlQuery like(Column column, boolean isLike, boolean pre, boolean end, Serializable data) {
        if (!StringUtils.isEmpty(data)) {
            whereQuery.like(column, isLike, pre, end, data);
        }
        return this;
    }

    private SqlQuery like(boolean isLike, boolean pre, boolean end, Column... columns) {
        if (columns != null) {
            for (Column column : columns) {
                whereQuery.like(column, isLike, pre, end);
            }
        }
        return this;
    }

    public SqlQuery isNull(Column... columns) {
        return concat("is null", "", columns);
    }

    public SqlQuery isNotNull(Column... columns) {
        return concat("is not null", "", columns);
    }

    public SqlQuery like(Column... columns) {
        return like(true, true, true, columns);
    }

    public SqlQuery like(Column column, Serializable data) {
        return like(column, true, true, true, data);
    }

    public SqlQuery notLike(Column... columns) {
        return like(false, true, true, columns);
    }

    public SqlQuery notLike(Column column, Serializable data) {
        return like(column, false, true, true, data);
    }

    public SqlQuery startingWith(Column... columns) {
        return like(true, false, true, columns);
    }

    public SqlQuery startingWith(Column column, Serializable data) {
        return like(column, true, false, true, data);
    }

    public SqlQuery endingWith(Column... columns) {
        return like(true, true, false, columns);
    }

    public SqlQuery endingWith(Column column, Serializable data) {
        return like(column, true, true, false, data);
    }

    public SqlQuery equal(Column... columns) {
        return concat("=", "", columns);
    }

    public SqlQuery equal(Column column, Serializable data) {
        return concatWithData(column, "=", "", data);
    }

    public SqlQuery equalIfNotNull(Column... columns) {
        return concatTest("=", "", columns);
    }

    public SqlQuery notEqual(Column... columns) {
        return concat("!=", "", columns);
    }

    public SqlQuery notEqual(Column column, Serializable data) {
        return concatWithData(column, "!=", "", data);
    }

    public SqlQuery notEqualIfNotNull(Column... columns) {
        return concatTest("!=", "", columns);
    }

    public SqlQuery lessThan(Column... columns) {
        return concat("<", "", columns);
    }

    public SqlQuery lessThan(Column column, Serializable data) {
        return concatWithData(column, "<", "", data);
    }

    public SqlQuery lessThanIfNotNull(Column... columns) {
        return concatTest("<", "", columns);
    }

    public SqlQuery lessThanEqual(Column... columns) {
        return concat("<=", "", columns);
    }

    public SqlQuery lessThanEqual(Column column, Serializable data) {
        return concatWithData(column, "<=", "", data);
    }

    public SqlQuery lessThanEqualIfNotNull(Column... columns) {
        return concatTest("<=", "", columns);
    }

    public SqlQuery greaterThan(Column... columns) {
        return concat(">", "", columns);
    }

    public SqlQuery greaterThan(Column column, Serializable data) {
        return concatWithData(column, ">", "", data);
    }

    public SqlQuery greaterThanIfNotNull(Column... columns) {
        return concatTest(">", "", columns);
    }

    public SqlQuery greaterThanEqual(Column... columns) {
        return concat(">=", "", columns);
    }

    public SqlQuery greaterThanEqual(Column column, Serializable data) {
        return concatWithData(column, ">=", "", data);
    }

    public SqlQuery greaterThanEqualIfNotNull(Column... columns) {
        return concatTest(">=", "", columns);
    }

    public SqlQuery in(Column column, Serializable... datas) {
        return concatWithData(column, "in (", ")", datas);
    }

    public SqlQuery notIn(Column column, Serializable... datas) {
        return concatWithData(column, " not in (", ")", datas);
    }

    /**
     * =====================
     * 排序语句
     * =====================
     */
    public SqlQuery orderBy(Column... columns) {
        whereQuery.orderBy(columns);
        return this;
    }

    public SqlQuery orderByDesc(Column... columns) {
        whereQuery.orderByDesc(columns);
        return this;
    }

    public SqlQuery groupBy(Column... columns) {
        whereQuery.groupBy(columns);
        return this;
    }

    /**
     * =========================
     * 链接相关代码
     * =========================
     */
    public SqlQuery or() {
        whereQuery.add(OR);
        return this;
    }

    public SqlQuery orNew() {
        whereQuery.add(AbstractSqlQuery.OR_NEW);
        return this;
    }

    public SqlQuery and() {
        whereQuery.add(AbstractSqlQuery.AND);
        return this;
    }

    public SqlQuery andNew() {
        whereQuery.add(AbstractSqlQuery.AND_NEW);
        return this;
    }

    public Class getEntityClass() {
        return entityClass;
    }
}
