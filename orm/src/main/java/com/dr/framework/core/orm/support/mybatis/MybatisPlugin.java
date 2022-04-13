package com.dr.framework.core.orm.support.mybatis;

import com.dr.framework.common.page.Page;
import com.dr.framework.core.orm.annotations.Table;
import com.dr.framework.core.orm.jdbc.Relation;
import com.dr.framework.core.orm.sql.support.SqlQuery;
import com.dr.framework.core.orm.support.mybatis.spring.MybatisConfigurationBean;
import com.dr.framework.core.orm.support.mybatis.spring.mapper.TableInfoProperties;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.parsing.GenericTokenParser;
import org.apache.ibatis.parsing.TokenHandler;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.ibatis.parsing.PropertyParser.KEY_DEFAULT_VALUE_SEPARATOR;
import static org.apache.ibatis.parsing.PropertyParser.KEY_ENABLE_DEFAULT_VALUE;

/**
 * 拦截ParameterHandler，动态设置sqlquery占位符
 * 拦截Executor 自动实现物理分页功能
 *
 * @author dr
 */
@Intercepts({
        /**
         * 拦截方法
         * @see Executor#update(MappedStatement, Object)
         */
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        /**
         * 拦截方法
         * @see Executor#query(MappedStatement, Object, RowBounds, ResultHandler)
         */
        @Signature(
                type = Executor.class, method = "query"
                , args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}
        ),
        /**
         * 拦截方法
         * @see Executor#query(MappedStatement, Object, RowBounds, ResultHandler, CacheKey, BoundSql)
         */
        @Signature(
                type = Executor.class, method = "query"
                , args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}
        )
})
public class MybatisPlugin implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws InvocationTargetException, IllegalAccessException, SQLException {
        Object[] args = invocation.getArgs();
        Executor executor = (Executor) invocation.getTarget();
        int argsCount = args.length;
        if (argsCount == 2) {
            return execUpdate(executor, (MappedStatement) args[0], args[1]);
        } else if (argsCount == 4) {
            return execQuery(executor, (MappedStatement) args[0], args[1], (RowBounds) args[2], (ResultHandler) args[3]);
        } else if (argsCount == 6) {
            return execQuery(executor, (MappedStatement) args[0], args[1], (RowBounds) args[2], (ResultHandler) args[3], (CacheKey) args[4], (BoundSql) args[5]);
        } else {
            return invocation.proceed();
        }
    }

    private class ParamHolder {
        SqlQuery sqlQuery;
        Class entityClass;
        Class returnClass;
        Relation relation;
        Object param;

        ParamHolder(MappedStatement ms, Object params) {
            MybatisConfigurationBean configuration = (MybatisConfigurationBean) ms.getConfiguration();
            this.param = params;
            if (params instanceof SqlQuery) {
                ((SqlQuery) params).remove(SqlQuery.QUERY_PARAM);
                bindRelationInfo(configuration, (SqlQuery) params);
                sqlQuery = (SqlQuery) params;
                entityClass = sqlQuery.getEntityClass();
                returnClass = sqlQuery.getReturnClass();
                relation = sqlQuery.getRelation();
            } else if (params instanceof Class) {
                entityClass = (Class) params;
                returnClass = entityClass;
                relation = configuration.getDataBaseService().getTableInfo(entityClass);
            } else if (params instanceof MapperMethod.ParamMap) {
                for (Object key : ((MapperMethod.ParamMap) params).keySet()) {
                    Object value = ((MapperMethod.ParamMap) params).get(key);
                    if (value instanceof SqlQuery) {
                        bindRelationInfo(configuration, (SqlQuery) params);
                        ((SqlQuery) value).put(SqlQuery.QUERY_PARAM, key);
                        entityClass = sqlQuery.getEntityClass();
                        returnClass = sqlQuery.getReturnClass();
                        relation = sqlQuery.getRelation();
                    } else if (value instanceof Class) {
                        entityClass = (Class) value;
                        returnClass = entityClass;
                        relation = configuration.getDataBaseService().getTableInfo(entityClass);
                    }
                }
            } else {
                Class clazz = params.getClass();
                if (clazz.isAnnotationPresent(Table.class)) {
                    entityClass = clazz;
                    returnClass = entityClass;
                    relation = configuration.getDataBaseService().getTableInfo(entityClass);
                }
            }
            Class returnType = ms.getResultMaps().get(0).getType();
            if (returnType != Object.class) {
                returnClass = returnType;
            }
        }
    }


    private Object execUpdate(Executor executor, MappedStatement ms, Object params) throws SQLException {
        ms = newMappedStatement(ms, params);
        return executor.update(ms, params);
    }


    private Object execQuery(Executor executor, MappedStatement ms, Object params, RowBounds rowBounds, ResultHandler resultHandler) throws SQLException {
        ms = newMappedStatement(ms, params);
        BoundSql boundSql = ms.getBoundSql(params);
        String organalSql = boundSql.getSql();
        String parsedSql = parseSql(organalSql);
        if (!isDefaultRowBounds(rowBounds)) {
            MybatisConfigurationBean configurationBean = (MybatisConfigurationBean) ms.getConfiguration();
            com.dr.framework.core.orm.database.Dialect dialect = configurationBean.getDataSourceProperties().getDialect();

            //拦截分页查询，设置最大分页，防止太大内存泄漏
            int limit = rowBounds.getLimit();
            if (limit > Page.getMaxPageSize()) {
                limit = Page.getMaxPageSize();
            }
            parsedSql = dialect.parseToPageSql(parsedSql, rowBounds.getOffset(), limit);
            rowBounds = RowBounds.DEFAULT;
        }
        boundSql = new BoundSqlWrapper(ms.getConfiguration(), parsedSql, boundSql, params);
        CacheKey cacheKey = executor.createCacheKey(ms, params, rowBounds, boundSql);
        return executor.query(ms, params, rowBounds, resultHandler, cacheKey, boundSql);
    }

    private Object execQuery(Executor executor
            , MappedStatement ms
            , Object params
            , RowBounds rowBounds
            , ResultHandler resultHandler
            , CacheKey cacheKey
            , BoundSql boundSql
    ) throws SQLException {
        ms = newMappedStatement(ms, params);
        String organalSql = boundSql.getSql();
        String parsedSql = parseSql(organalSql);
        if (isDefaultRowBounds(rowBounds)) {
            MybatisConfigurationBean configurationBean = (MybatisConfigurationBean) ms.getConfiguration();
            com.dr.framework.core.orm.database.Dialect dialect = configurationBean.getDataSourceProperties().getDialect();
            parsedSql = dialect.parseToPageSql(parsedSql, rowBounds.getOffset(), rowBounds.getLimit());
            rowBounds = RowBounds.DEFAULT;
        }
        boundSql = new BoundSqlWrapper(ms.getConfiguration(), parsedSql, boundSql, params);
        cacheKey = executor.createCacheKey(ms, params, rowBounds, boundSql);
        return executor.query(ms, params, rowBounds, resultHandler, cacheKey, boundSql);
    }

    static class BoundSqlWrapper extends BoundSql {
        BoundSql boundSql;
        String sql;

        public BoundSqlWrapper(Configuration configuration, String sql, BoundSql boundSql, Object params) {
            super(configuration, sql, boundSql.getParameterMappings(), params);
            this.boundSql = boundSql;
            this.sql = sql;
        }

        @Override
        public String getSql() {
            return sql;
        }

        @Override
        public boolean hasAdditionalParameter(String name) {
            return boundSql.hasAdditionalParameter(name);
        }

        @Override
        public Object getAdditionalParameter(String name) {
            return boundSql.getAdditionalParameter(name);
        }

        @Override
        public void setAdditionalParameter(String name, Object value) {
            boundSql.setAdditionalParameter(name, value);
        }
    }


    private boolean isDefaultRowBounds(RowBounds bounds) {
        return bounds != null && bounds.getOffset() == RowBounds.NO_ROW_OFFSET && bounds.getLimit() == RowBounds.NO_ROW_LIMIT;
    }


    /**
     * 绑定EntityRelation到sqlquery
     *
     * @param configurationBean
     * @param params
     */
    private void bindRelationInfo(MybatisConfigurationBean configurationBean, SqlQuery params) {
        if (configurationBean != null) {
            if (params.getRelation() == null) {
                if (params.getEntityClass() != null) {
                    Relation relation = configurationBean.getDataBaseService().getTableInfo(params.getEntityClass());
                    Assert.notNull(relation, "类" + params.getEntityClass() + "没有@Table注解，或者声明数据源没有包含此类所属模块");
                    params.bindRelation(relation);
                }
            }
            List<SqlQuery> children = params.children();
            if (children != null) {
                children.forEach(c -> bindRelationInfo(configurationBean, c));
            }
            //绑定方言
            params.put(SqlQuery.DIALECT_KEY, configurationBean.getDataSourceProperties().getDialect());
        }
    }

    static class VariableTokenHandler implements TokenHandler {
        private final Properties variables;
        private final boolean enableDefaultValue;
        private final String defaultValueSeparator;

        private VariableTokenHandler(Properties variables) {
            this.variables = variables;
            this.enableDefaultValue = Boolean.parseBoolean(getPropertyValue(KEY_ENABLE_DEFAULT_VALUE, "false"));
            this.defaultValueSeparator = getPropertyValue(KEY_DEFAULT_VALUE_SEPARATOR, ":");
        }

        private String getPropertyValue(String key, String defaultValue) {
            return (variables == null) ? defaultValue : variables.getProperty(key, defaultValue);
        }

        @Override
        public String handleToken(String content) {
            if (variables != null) {
                String key = content;
                if (enableDefaultValue) {
                    final int separatorIndex = content.indexOf(defaultValueSeparator);
                    String defaultValue = null;
                    if (separatorIndex >= 0) {
                        key = content.substring(0, separatorIndex);
                        defaultValue = content.substring(separatorIndex + defaultValueSeparator.length());
                    }
                    if (defaultValue != null) {
                        return variables.getProperty(key, defaultValue);
                    }
                }
                if (variables.containsKey(key)) {
                    return variables.getProperty(key);
                }
            }
            return "${" + content + "}";
        }
    }


    private MappedStatement newMappedStatement(MappedStatement ms, Object params) {
        Configuration configuration = ms.getConfiguration();
        if (configuration instanceof MybatisConfigurationBean) {
            MybatisConfigurationBean configurationBean = (MybatisConfigurationBean) configuration;
            ParamHolder paramHolder = new ParamHolder(ms, params);
            if (paramHolder.sqlQuery == null && paramHolder.entityClass == null) {
                return ms;
            }
            String msId = buildStatementId(ms, paramHolder.relation, paramHolder.returnClass);
            return mappedStatementMap.computeIfAbsent(msId, k -> {
                BoundSql boundSql = ms.getBoundSql(params);
                //TODO  这里可能有个bug  MappedStatement里面声明parameterMap的时候会丢失数据  应该也没多大问题
                String sql = boundSql.getSql();
                GenericTokenParser parser = new GenericTokenParser("!!{", "}", new VariableTokenHandler(
                        new TableInfoProperties(
                                paramHolder.relation
                                , configurationBean.getDataSourceProperties().getDialect()
                                , ms.getSqlCommandType().equals(SqlCommandType.INSERT)
                                , paramHolder.sqlQuery != null
                                , paramHolder.sqlQuery == null ? null : (String) paramHolder.sqlQuery.get(SqlQuery.QUERY_PARAM)
                        )
                ));
                sql = parser.parse(sql).trim();
                List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
                int size = parameterMappings.size();
                int index = 0;
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < sql.length(); i++) {
                    char c = sql.charAt(i);
                    if (c == '?') {
                        Assert.isTrue(index < size, "转换sql语句失败！");
                        ParameterMapping parameterMapping = parameterMappings.get(index);
                        sb.append("#{")
                                .append(parameterMapping.getProperty());
                        if (parameterMapping.getJdbcType() != null) {
                            sb.append(",jdbctype=")
                                    .append(parameterMapping.getJdbcType().toString());
                        }

                        sb.append('}');
                        index++;
                    } else {
                        sb.append(c);
                    }
                }
                sql = sb.toString();
                if (!sql.startsWith("<script>") || !sql.startsWith("<SCRIPT>")) {
                    sql = "<script>" + sql + "</script>";
                }
                SqlSource sqlSource = ms.getLang().createSqlSource(configurationBean, sql, ms.getParameterMap().getType());
                MappedStatement.Builder builder = new MappedStatement.Builder(configuration, k, sqlSource, ms.getSqlCommandType());

                builder.resource(ms.getResource())
                        .fetchSize(ms.getFetchSize())
                        .statementType(ms.getStatementType())
                        .keyGenerator(ms.getKeyGenerator());
                if (ms.getKeyProperties() != null && ms.getKeyProperties().length != 0) {
                    StringBuilder keyProperties = new StringBuilder();
                    for (String keyProperty : ms.getKeyProperties()) {
                        keyProperties.append(keyProperty).append(",");
                    }
                    keyProperties.delete(keyProperties.length() - 1, keyProperties.length());
                    builder.keyProperty(keyProperties.toString());
                }

                ParameterMap parameterMap = new ParameterMap.Builder(ms.getConfiguration()
                        , ms.getParameterMap().getId()
                        , ms.getParameterMap().getType()
                        , new ArrayList<>(0))
                        .build();

                builder.timeout(ms.getTimeout())
                        .parameterMap(parameterMap)
                        .resultMaps(Collections.singletonList(
                                new ResultMap.Builder(ms.getConfiguration()
                                        , ms.getId()
                                        , paramHolder.returnClass
                                        //TODO 这里应该有问题
                                        , Collections.EMPTY_LIST)
                                        .build()))
                        .resultSetType(ms.getResultSetType())
                        .cache(ms.getCache())
                        .flushCacheRequired(ms.isFlushCacheRequired())
                        .useCache(ms.isUseCache());
                return builder.build();
            });
        }
        return ms;
    }

    /**
     * 缓存动态生成的MappedStatement
     * key 由原始id+"_"+module+"_"+table+"_"+returnClass组成
     */
    private Map<String, MappedStatement> mappedStatementMap = Collections.synchronizedMap(new HashMap<>(128));

    private String parseSql(String organalSql) {
        String newSql = organalSql;
        if (StringUtils.hasText(newSql)) {
            newSql = newSql.trim();
            if (newSql.endsWith("where") || newSql.endsWith("WHERE")) {
                newSql = newSql.substring(0, newSql.length() - 5).trim();
            }
            if (newSql.endsWith("and") || newSql.endsWith("AND")) {
                newSql = newSql.substring(0, newSql.length() - 3).trim();
            }
        }
        return newSql;
    }

    private String buildStatementId(MappedStatement ms, Relation relation, Class resultType) {
        Stream<String> stream = relation == null ?
                Stream.of(ms.getId(), resultType.getSimpleName()) :
                Stream.of(ms.getId(), relation.getModule(), relation.getName(), resultType.getName());
        return stream.collect(Collectors.joining("_"));
    }


    @Override
    public Object plugin(Object target) {
        if (target instanceof Executor) {
            target = Plugin.wrap(target, this);
        }
        return target;
    }

    @Override
    public void setProperties(Properties properties) {
    }
}
