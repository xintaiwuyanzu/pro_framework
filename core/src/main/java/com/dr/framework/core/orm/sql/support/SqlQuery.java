package com.dr.framework.core.orm.sql.support;

import com.dr.framework.core.orm.annotations.Table;
import com.dr.framework.core.orm.database.Dialect;
import com.dr.framework.core.orm.jdbc.Relation;
import com.dr.framework.core.orm.module.EntityRelation;
import com.dr.framework.core.orm.sql.Column;
import com.dr.framework.core.orm.sql.TableInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.core.ReflectUtils;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 查询sql使用的工具类
 * TODO
 * 联合查询
 * 自关联查询
 * having
 * @author dr
 */
public final class SqlQuery<E> extends HashMap<String, Object> {
    public static final String COLUMNS = "!!{query#$columns}";
    public static final String FROM = "!!{query#$from}";
    public static final String TABLE = "!!{query#$table}";
    public static final String WHERE = "!!{query#$where}";
    public static final String WHERE_NO_ORERY_BY = "!!{query#$whereNO}";
    public static final String QUERY_CLASS_SUFFIX = "Info";
    public static final String QUERY_PARAM = "$QP";
    public static final String DIALECT_KEY = "$dialect";

    protected static final String ENTITY_KEY = "ENTITY";

    private static final String ENTITY_CLASS_KEY = "ENTITY_CLASS";
    private static final String RETURN_CLASS_KEY = "RETURN_CLASS";
    private static final String MAP_KEY_KEY = "$MAP_KEY";
    private static final String PARENT_KEY = "$PARENT_KEY";

    static protected Logger logger = LoggerFactory.getLogger(SqlQuery.class);
    private static Map<Class, Class<? extends TableInfo>> sqlQueryMap = Collections.synchronizedMap(new HashMap<>());

    /**
     * 根据model类获取该类的query帮助类
     *
     * @param modelClass
     * @param <T>
     * @return
     */
    public static synchronized <T extends TableInfo> T getTableInfo(Class modelClass) {
        if (!sqlQueryMap.containsKey(modelClass)) {
            loadTableInfo(modelClass);
        }
        if (sqlQueryMap.containsKey(modelClass)) {
            try {
                return (T) ReflectUtils.newInstance(sqlQueryMap.get(modelClass));
            } catch (Exception e) {
                logger.error("获取" + modelClass.getName() + "表基本信息失败,请检查" + modelClass.getName() + "TableInfo是否生成！", modelClass);
            }
        }
        //TODO 有的没有生成info类
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

    public static <R> SqlQuery<R> from(R entity) {
        return from(entity, null, false);
    }

    public static <R> SqlQuery<R> from(R entity, boolean selectAllColumns) {
        return from(entity, null, selectAllColumns);
    }

    public static <R> SqlQuery<R> from(R entity, String alia) {
        return from(entity, alia, false);
    }

    public static <R> SqlQuery<R> from(R entity, String alias, boolean selectAllColumns) {
        SqlQuery sqlQuery = from(entity.getClass(), alias, selectAllColumns);
        if (entity != null) {
            sqlQuery.put(ENTITY_KEY, entity);
        }
        return sqlQuery;
    }

    public static <R> SqlQuery<R> from(Class<R> entityClass) {
        return from(entityClass, null, true);
    }

    public static <R> SqlQuery<R> from(Class<R> entityClass, String alias) {
        return from(entityClass, alias, false);
    }

    public static <R> SqlQuery<R> from(Class<R> entityClass, boolean selectAllColumns) {
        return from(entityClass, null, selectAllColumns);
    }

    public static <R> SqlQuery<R> from(Relation relation) {
        return from(relation, null, true);
    }

    public static <R> SqlQuery<R> from(Relation relation, String alias) {
        return from(relation, alias, true);
    }

    public static <R> SqlQuery<R> from(Relation relation, boolean selectAllColumns) {
        return from(relation, null, selectAllColumns);
    }

    public static <R> SqlQuery<R> from(Relation relation, String alias, boolean selectAllColumns) {
        SqlQuery query = new SqlQuery();
        if (relation instanceof EntityRelation) {
            Class<R> c = ((EntityRelation) relation).getEntityClass();
            query.setReturnClass(c).put(ENTITY_CLASS_KEY, c);
        }
        query.fromQuery.from(relation.getName(), alias);
        query.columnsQuery.setIncludeAll(selectAllColumns);
        query.columnsQuery.bindRelation(relation);
        query.put("$relation", relation);
        return query;
    }

    public static <R> SqlQuery<R> from(Class<R> entityClass, String alias, boolean selectAllColumns) {
        SqlQuery query = new SqlQuery();
        Class ec = entityClass;
        while (true) {
            if (ec == Object.class) {
                ec = null;
                break;
            } else if (ec.isAnnotationPresent(Table.class)) {
                break;
            } else {
                ec = ec.getSuperclass();
            }
        }
        Assert.notNull(ec, "未能查询到要操作的表：" + entityClass + "，没有实现@Table注解！");
        query.setReturnClass(ec).put(ENTITY_CLASS_KEY, ec);
        Table table = (Table) ec.getAnnotation(Table.class);
        //强制模块化概念
        Assert.isTrue(table.genInfo(), () -> "实体类【" + entityClass + "】对应的表【" + table.name() + "】不能直接操作，请调用相关的service操作");
        query.fromQuery.from(table.name(), alias);
        query.fromQuery.aliasClass(ec, alias);
        query.columnsQuery.setIncludeAll(selectAllColumns);
        return query;
    }

    /**
     * =================================================
     * 以上是静态方法，下面是实例方法和变量
     * =================================================
     */
    private WhereQuery whereQuery = new WhereQuery();
    private FromQuery fromQuery = new FromQuery();
    private ColumnsQuery columnsQuery = new ColumnsQuery();
    private SetQuery setQuery = new SetQuery();

    public SqlQuery() {
        super(32);
        put("$whereQuery", whereQuery);
        put("$fromQuery", fromQuery);
        put("$columnsQuery", columnsQuery);
        put("$setQuery", setQuery);
    }

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
        if (Objects.isNull(value)) {
            String keyStr = key.toString().trim().toLowerCase();
            switch (keyStr) {
                case "$set":
                    value = setQuery.sql(fromQuery.tableAlias, this);
                    break;
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
                        value = fromQuery.tableAlias.alias(table);
                    }
                    break;
            }
        }
        return value;
    }

    @Override
    public String toString() {
        return new StringBuilder(get("$columns").toString())
                .append(get("$from"))
                .append(get("$table"))
                .append(get("$where"))
                .toString();
    }

    /**
     * ==================
     * set相关
     * ==================
     */
    public SqlQuery<E> set(Column column, Serializable data) {
        put(column.getAlias(), data);
        put("$$set" + column.getAlias(), true);
        return this;
    }

    public SqlQuery<E> set(Column left, Column right) {
        if (left.getTable().equalsIgnoreCase(right.getTable())) {
            if (left.getName().equalsIgnoreCase(right.getName())) {
                logger.warn("不能设置相同的列");
            } else {
                setQuery.column(left, right);
            }
        } else {
            logger.warn("左列与右列应该是同一张表");
        }
        return this;
    }


    /**
     * ==================================
     * column相关
     * ==================================
     */

    public SqlQuery<E> alias(String table, String alias) {
        fromQuery.alias(table, alias);
        return this;
    }

    public SqlQuery<E> column(Column... columns) {
        columnsQuery.column(columns);
        return this;
    }

    public SqlQuery<E> max(Column column, String alias) {
        columnsQuery.column(Column.max(column, alias));
        return this;
    }

    public SqlQuery<E> max(Column... columns) {
        for (Column column : columns) {
            max(column, null);
        }
        return this;
    }

    public SqlQuery<E> min(Column column, String alias) {
        columnsQuery.column(Column.min(column, alias));
        return this;
    }

    public SqlQuery<E> min(Column... columns) {
        for (Column column : columns) {
            min(column, null);
        }
        return this;
    }

    public SqlQuery<E> count(Column column, String alias) {
        return count(column, alias, false);
    }

    public SqlQuery<E> count(Column column, String alias, boolean desc) {
        return count(column, alias, true, desc);
    }

    public SqlQuery<E> count(Column column, String alias, boolean orderBy, boolean desc) {
        columnsQuery.column(Column.count(column, alias));
        if (orderBy) {
            whereQuery.orderBy(alias, desc);
        }
        return this;
    }

    public SqlQuery<E> count(Column... columns) {
        for (Column column : columns) {
            count(column, null);
        }
        return this;
    }

    public SqlQuery<E> exclude(Column... columns) {
        columnsQuery.exclude(columns);
        return this;
    }

    /**
     * =======================
     * from相关
     * =======================
     */
    public SqlQuery<E> join(Column left, Column right) {
        fromQuery.join(left, right);
        return this;
    }

    public SqlQuery<E> innerJoin(Column left, Column right) {
        fromQuery.innerJoin(left, right);
        return this;
    }

    /**
     * select a.*,b.* from a left join b on a.left=b.right
     *
     * @param left
     * @param right
     * @return
     */
    public SqlQuery<E> leftOuterJoin(Column left, Column right) {
        fromQuery.leftOuterJoin(left, right);
        return this;
    }

    public SqlQuery<E> rightOuterJoin(Column left, Column right) {
        fromQuery.rightOuterJoin(left, right);
        return this;
    }

    public SqlQuery<E> outerJoin(Column left, Column right) {
        fromQuery.outerJoin(left, right);
        return this;
    }


    /**
     * =======================
     * where相关
     * =======================
     */
    private SqlQuery<E> concat(String prefix, String suffix, Column... columns) {
        if (columns != null) {
            for (Column column : columns) {
                whereQuery.concatSql(column, prefix, suffix);
            }
        }
        return this;
    }

    private SqlQuery<E> concatTest(String prefix, String suffix, Column... columns) {
        if (columns != null) {
            for (Column column : columns) {
                whereQuery.concatTestSql(column, prefix, suffix);
            }
        }
        return this;
    }

    private SqlQuery<E> concatWithData(Column column, String prefix, String suffix, Serializable... data) {
        whereQuery.concatSqlWithData(column, prefix, suffix, data);
        return this;
    }

    private SqlQuery<E> concatWithSubQuery(Column column, String prefix, String suffix, SqlQuery query) {
        List<SqlQuery> sqlQueries = (List<SqlQuery>) computeIfAbsent("$children", k -> new ArrayList<>());
        sqlQueries.add(query);
        whereQuery.concatSqlWithQuery(column, prefix, suffix, query);
        return this;
    }

    private SqlQuery<E> like(Column column, boolean isLike, boolean pre, boolean end, Serializable data) {
        if (!ObjectUtils.isEmpty(data)) {
            whereQuery.like(column, isLike, pre, end, data);
        }
        return this;
    }

    private SqlQuery<E> like(boolean isLike, boolean pre, boolean end, Column... columns) {
        if (columns != null) {
            for (Column column : columns) {
                whereQuery.like(column, isLike, pre, end);
            }
        }
        return this;
    }

    public SqlQuery<E> isNull(Column... columns) {
        for (Column column : columns) {
            if (column != null) {
                whereQuery.pureSql(column, " is null");
            }
        }
        return this;
    }

    public SqlQuery<E> isNotNull(Column... columns) {
        for (Column column : columns) {
            if (column != null) {
                whereQuery.pureSql(column, " is not null");
            }
        }
        return this;
    }

    public SqlQuery<E> like(Column... columns) {
        return like(true, true, true, columns);
    }

    public SqlQuery<E> like(Column column, Serializable data) {
        return like(column, true, true, true, data);
    }

    public SqlQuery<E> notLike(Column... columns) {
        return like(false, true, true, columns);
    }

    public SqlQuery<E> notLike(Column column, Serializable data) {
        return like(column, false, true, true, data);
    }

    public SqlQuery<E> notStartingWith(Column... columns) {
        return like(false, false, true, columns);
    }

    public SqlQuery<E> notStartingWith(Column column, Serializable data) {
        return like(column, false, false, true, data);
    }

    public SqlQuery<E> notEndingWith(Column... columns) {
        return like(false, true, false, columns);
    }

    public SqlQuery<E> notEndingWith(Column column, Serializable data) {
        return like(column, false, true, false, data);
    }

    public SqlQuery<E> startingWith(Column... columns) {
        return like(true, false, true, columns);
    }

    public SqlQuery<E> startingWith(Column column, Serializable data) {
        return like(column, true, false, true, data);
    }

    public SqlQuery<E> endingWith(Column... columns) {
        return like(true, true, false, columns);
    }

    public SqlQuery<E> endingWith(Column column, Serializable data) {
        return like(column, true, true, false, data);
    }

    public SqlQuery<E> equal(Column... columns) {
        return concat("=", "", columns);
    }

    public SqlQuery<E> equal(Column column, Serializable data) {
        return concatWithData(column, "=", "", data);
    }

    public SqlQuery<E> equal(Column column, SqlQuery query) {
        return concatWithSubQuery(column, "=", "", query);
    }

    public SqlQuery<E> equalIfNotNull(Column... columns) {
        return concatTest("=", "", columns);
    }

    public SqlQuery<E> notEqual(Column... columns) {
        return concat("!=", "", columns);
    }

    public SqlQuery<E> notEqual(Column column, Serializable data) {
        return concatWithData(column, "!=", "", data);
    }

    public SqlQuery<E> notEqual(Column column, SqlQuery query) {
        return concatWithSubQuery(column, "!=", "", query);
    }

    public SqlQuery<E> notEqualIfNotNull(Column... columns) {
        return concatTest("!=", "", columns);
    }

    public SqlQuery<E> lessThan(Column... columns) {
        return concat("<", "", columns);
    }

    public SqlQuery<E> lessThan(Column column, SqlQuery query) {
        return concatWithSubQuery(column, "<", "", query);
    }

    public SqlQuery<E> lessThan(Column column, Serializable data) {
        return concatWithData(column, "<", "", data);
    }

    public SqlQuery<E> lessThanIfNotNull(Column... columns) {
        return concatTest("<", "", columns);
    }

    public SqlQuery<E> lessThanEqual(Column... columns) {
        return concat("<=", "", columns);
    }

    public SqlQuery<E> lessThanEqual(Column column, Serializable data) {
        return concatWithData(column, "<=", "", data);
    }

    public SqlQuery<E> lessThanEqualIfNotNull(Column... columns) {
        return concatTest("<=", "", columns);
    }

    public SqlQuery<E> greaterThan(Column... columns) {
        return concat(">", "", columns);
    }

    public SqlQuery<E> greaterThan(Column column, Serializable data) {
        return concatWithData(column, ">", "", data);
    }

    public SqlQuery<E> greaterThan(Column column, SqlQuery query) {
        return concatWithSubQuery(column, ">", "", query);
    }

    public SqlQuery<E> greaterThanIfNotNull(Column... columns) {
        return concatTest(">", "", columns);
    }

    public SqlQuery<E> greaterThanEqual(Column... columns) {
        return concat(">=", "", columns);
    }

    public SqlQuery<E> greaterThanEqual(Column column, Serializable data) {
        return concatWithData(column, ">=", "", data);
    }

    public SqlQuery<E> greaterThanEqualIfNotNull(Column... columns) {
        return concatTest(">=", "", columns);
    }


    public SqlQuery<E> in(Column column, SqlQuery query) {
        return concatWithSubQuery(column, "in", "", query);
    }

    /**
     * TODO 先用丑陋的方法实现
     *
     * @param column
     * @param datas
     * @return
     */
    public SqlQuery<E> in(Column column, Serializable... datas) {
        if (datas != null && datas.length > 0) {
            whereQuery.pureSql(column, " in ("
                    + Arrays.stream(datas)
                    .map(serializable -> String.format("'%s'", WhereQuery.cleanXSS(serializable)))
                    .collect(Collectors.joining(","))
                    + ")");
        }
        return this;
    }

    /**
     * TODO 可以通过简单的方式实现，但是要考虑多次查询的情况
     * TODO 要过滤大量in条件的情况
     * {@link WhereQuery#collectionSql(Column, String, String, Collection)}
     *
     * @param column
     * @param datas
     * @return
     */
    public SqlQuery<E> in(Column column, List<? extends Serializable> datas) {
        if (datas != null && !datas.isEmpty()) {
            whereQuery.pureSql(column, " in ("
                    + datas.stream()
                    .map(serializable -> String.format("'%s'", WhereQuery.cleanXSS(serializable)))
                    .collect(Collectors.joining(","))
                    + ")");
        }
        return this;
    }

    /**
     * TODO 先用丑陋的方法实现
     *
     * @param column
     * @param datas
     * @return
     */
    public SqlQuery<E> notIn(Column column, Serializable... datas) {
        if (datas != null && datas.length > 0) {
            whereQuery.pureSql(column, " not in (" +
                    Arrays.stream(datas)
                            .map(serializable -> String.format("'%s'", WhereQuery.cleanXSS(serializable)))
                            .collect(Collectors.joining(","))
                    + ")");
        }
        return this;
    }

    public SqlQuery<E> notIn(Column column, SqlQuery query) {
        return concatWithSubQuery(column, "not in", "", query);
    }


    public SqlQuery<E> notIn(Column column, List<? extends Serializable> datas) {
        if (datas != null && !datas.isEmpty()) {
            whereQuery.pureSql(column, " not in (" +
                    datas.stream()
                            .map(serializable -> String.format("'%s'", WhereQuery.cleanXSS(serializable)))
                            .collect(Collectors.joining(","))
                    + ")");
        }
        return this;
    }

    public SqlQuery<E> between(Column column, Comparable start, Comparable end) {
        whereQuery.between(column, start, end);
        return this;
    }

    /**
     * =====================
     * 排序语句
     * =====================
     */
    public SqlQuery<E> orderBy(Column... columns) {
        whereQuery.orderBy(columns);
        return this;
    }

    public SqlQuery<E> orderByDesc(Column... columns) {
        whereQuery.orderByDesc(columns);
        return this;
    }

    public SqlQuery<E> groupBy(Column... columns) {
        whereQuery.groupBy(columns);
        return this;
    }

    /**
     * =========================
     * 链接相关代码
     * =========================
     */
    public SqlQuery<E> or() {
        whereQuery.add(AbstractSqlQuery.OR);
        return this;
    }

    public SqlQuery<E> orNew() {
        whereQuery.add(AbstractSqlQuery.OR_NEW);
        return this;
    }

    public SqlQuery<E> and() {
        whereQuery.add(AbstractSqlQuery.AND);
        return this;
    }

    public SqlQuery<E> andNew() {
        whereQuery.add(AbstractSqlQuery.AND_NEW);
        return this;
    }

    public Class getEntityClass() {
        return (Class) get(ENTITY_CLASS_KEY);
    }

    public Class<E> getReturnClass() {
        return (Class) get(RETURN_CLASS_KEY);
    }

    public <R> SqlQuery<R> setReturnClass(Class<R> returnClass) {
        put(RETURN_CLASS_KEY, returnClass);
        return (SqlQuery<R>) this;
    }

    SqlQuery getParent() {
        return (SqlQuery) get(PARENT_KEY);
    }

    public void bindRelation(Relation<? extends Column> relation) {
        put("$relation", relation);
        columnsQuery.bindRelation(relation);
        fromQuery.bindRelation(relation);
    }

    public Relation getRelation() {
        return (Relation) get("$relation");
    }


    String getMapKey() {
        return (String) get(MAP_KEY_KEY);
    }


    void setParent(SqlQuery parent, String key) {
        put(PARENT_KEY, parent);
        put(MAP_KEY_KEY, key);
    }

    public List<SqlQuery> children() {
        return (List<SqlQuery>) get("$children");
    }

    int columnSize() {
        return columnsQuery.columnSize();
    }

    protected Dialect getDialect() {
        return (Dialect) get(DIALECT_KEY);
    }
}
