package com.dr.framework.core.orm.support.mybatis;

import com.dr.framework.core.orm.sql.support.SqlQuery;
import com.dr.framework.core.orm.support.mybatis.spring.MybatisConfigurationBean;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.*;

/**
 * 拦截ParameterHandler，动态设置sqlquery占位符
 * 拦截Executor 自动实现物理分页功能
 *
 * @author dr
 */
@Intercepts({@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class})})
public class MybatisPlugin implements Interceptor {
    private static final List<ResultMapping> EMPTY_RESULTMAPPING = new ArrayList(0);

    @Override
    public Object intercept(Invocation invocation) throws InvocationTargetException, IllegalAccessException, SQLException {
        Executor executor = (Executor) invocation.getTarget();
        Object[] args = invocation.getArgs();
        MappedStatement mappedStatement = (MappedStatement) args[0];
        Object parameter = args[1];
        //给sqlquery设置占位符
        mappedStatement = parseSqlQuery(parameter, mappedStatement);

        int argsCount = args.length;
        if (argsCount > 2) {
            RowBounds rowBounds = (RowBounds) args[2];
            ResultHandler resultHandler = (ResultHandler) args[3];
            CacheKey cacheKey = null;
            BoundSql boundSql;
            //确定是query方法
            if (argsCount >= 4) {
                if (argsCount == 6) {
                    cacheKey = (CacheKey) args[4];
                    boundSql = (BoundSql) args[5];
                } else {
                    boundSql = mappedStatement.getBoundSql(parameter);
                }
                String organalSql = boundSql.getSql();
                String parsedSql = parseSql(organalSql);
                if (!isDefaultRowBounds(rowBounds)) {
                    MybatisConfigurationBean configurationBean = (MybatisConfigurationBean) mappedStatement.getConfiguration();
                    com.dr.framework.core.orm.database.Dialect dialect = configurationBean.getDataSourceProperties().getDialect();
                    parsedSql = dialect.parseToPageSql(parsedSql, rowBounds.getOffset(), rowBounds.getLimit());
                    rowBounds = RowBounds.DEFAULT;
                }
                if (!organalSql.equalsIgnoreCase(parsedSql)) {
                    boundSql = new BoundSql(mappedStatement.getConfiguration(), parsedSql, boundSql.getParameterMappings(), parameter);
                    cacheKey = executor.createCacheKey(mappedStatement, parameter, rowBounds, boundSql);
                }
                if (cacheKey == null) {
                    cacheKey = executor.createCacheKey(mappedStatement, parameter, rowBounds, boundSql);
                }

                if (rowBounds.getLimit() == rowBounds.getOffset() && rowBounds.getOffset() == 0) {
                    return Collections.emptyList();
                }
                return executor.query(mappedStatement, parameter, rowBounds, resultHandler, cacheKey, boundSql);
            }
        }
        return invocation.proceed();
    }


    private boolean isDefaultRowBounds(RowBounds bounds) {
        return bounds != null && bounds.getOffset() == RowBounds.NO_ROW_OFFSET && bounds.getLimit() == RowBounds.NO_ROW_LIMIT;
    }


    private String parseSql(String organalSql) {
        String newSql = organalSql;
        if (!StringUtils.isEmpty(newSql)) {
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

    /**
     * 处理sqlquery参数，根据sqlquery参数位置动态设置sqlquery的参数占位符
     *
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private MappedStatement parseSqlQuery(Object parameterObject, MappedStatement mappedStatement) {
        if (parameterObject instanceof SqlQuery) {
            ((SqlQuery) parameterObject).remove(SqlQuery.QUERY_PARAM);
            mappedStatement = checkReturnType((SqlQuery) parameterObject, mappedStatement);
        } else if (parameterObject instanceof MapperMethod.ParamMap) {
            for (Object key : ((MapperMethod.ParamMap) parameterObject).keySet()) {
                Object value = ((MapperMethod.ParamMap) parameterObject).get(key);
                if (value instanceof SqlQuery) {
                    ((SqlQuery) value).put(SqlQuery.QUERY_PARAM, key);
                    mappedStatement = checkReturnType((SqlQuery) parameterObject, mappedStatement);
                }
            }
        }
        return mappedStatement;
    }

    private MappedStatement checkReturnType(SqlQuery sqlQuery, MappedStatement mappedStatement) {
        if (!sqlQuery.getEntityClass().equals(sqlQuery.getReturnClass())) {
            return newMappedStatement(mappedStatement, sqlQuery.getReturnClass());
        }
        return mappedStatement;
    }

    private MappedStatement newMappedStatement(MappedStatement ms, Class resultType) {
        //下面是新建的过程，考虑效率和复用对象的情况下，这里最后生成的ms可以缓存起来，下次根据 ms.getId() + "_" + getShortName(resultType) 直接返回 ms,省去反复创建的过程
        MappedStatement.Builder builder = new MappedStatement.Builder(ms.getConfiguration(), ms.getId() + "_" + getShortName(resultType), ms.getSqlSource(), ms.getSqlCommandType());
        builder.resource(ms.getResource());
        builder.fetchSize(ms.getFetchSize());
        builder.statementType(ms.getStatementType());
        builder.keyGenerator(ms.getKeyGenerator());
        if (ms.getKeyProperties() != null && ms.getKeyProperties().length != 0) {
            StringBuilder keyProperties = new StringBuilder();
            for (String keyProperty : ms.getKeyProperties()) {
                keyProperties.append(keyProperty).append(",");
            }
            keyProperties.delete(keyProperties.length() - 1, keyProperties.length());
            builder.keyProperty(keyProperties.toString());
        }
        builder.timeout(ms.getTimeout());
        builder.parameterMap(ms.getParameterMap());
        //count查询返回值int
        builder.resultMaps(Arrays.asList(
                new ResultMap.Builder(ms.getConfiguration()
                        , ms.getId()
                        , resultType
                        , EMPTY_RESULTMAPPING)
                        .build()));
        builder.resultSetType(ms.getResultSetType());
        builder.cache(ms.getCache());
        builder.flushCacheRequired(ms.isFlushCacheRequired());
        builder.useCache(ms.isUseCache());
        return builder.build();
    }

    private String getShortName(Class clazz) {
        String className = clazz.getCanonicalName();
        return className.substring(className.lastIndexOf(".") + 1);
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
