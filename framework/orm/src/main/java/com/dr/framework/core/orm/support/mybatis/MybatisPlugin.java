package com.dr.framework.core.orm.support.mybatis;

import com.dr.framework.core.orm.sql.support.SqlQuery;
import com.dr.framework.core.orm.support.mybatis.spring.MybatisConfigurationBean;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Properties;

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
    @Override
    public Object intercept(Invocation invocation) throws InvocationTargetException, IllegalAccessException, SQLException {
        Executor executor = (Executor) invocation.getTarget();
        Object[] args = invocation.getArgs();
        MappedStatement mappedStatement = (MappedStatement) args[0];

        Object parameter = args[1];
        //给sqlquery设置占位符
        parseSqlQuery(parameter);

        int argsCount = args.length;
        if (argsCount > 2) {
            RowBounds rowBounds = (RowBounds) args[2];
            ResultHandler resultHandler = (ResultHandler) args[3];
            CacheKey cacheKey = null;
            BoundSql boundSql = null;
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
    private void parseSqlQuery(Object parameterObject) {
        if (parameterObject instanceof SqlQuery) {
            ((SqlQuery) parameterObject).remove(SqlQuery.QUERY_PARAM);
        } else if (parameterObject instanceof MapperMethod.ParamMap) {
            for (Object key : ((MapperMethod.ParamMap) parameterObject).keySet()) {
                Object value = ((MapperMethod.ParamMap) parameterObject).get(key);
                if (value instanceof SqlQuery) {
                    ((SqlQuery) value).put(SqlQuery.QUERY_PARAM, key);
                }
            }
        }
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
