package com.dr.framework.core.orm.support.mybatis.spring.mapper.method;

import org.apache.ibatis.binding.BindingException;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.StatementType;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.springframework.util.Assert;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class MapperMethod {
    private final MethodSignature method;
    private Configuration config;

    public MapperMethod(Class<?> mapperInterface, Method method, Configuration config) {
        this.method = new MethodSignature(config, mapperInterface, method);
        this.config = config;
    }

    public Object execute(SqlSession sqlSession, Object[] args, Class<?> entityClass) {
        Object result;
        String statementId = method.getName(entityClass);
        MappedStatement mappedStatement = null;
        SqlCommandType sqlCommandType;
        if (method.isFlush()) {
            sqlCommandType = SqlCommandType.FLUSH;
        } else {
            try {
                mappedStatement = config.getMappedStatement(statementId);
            } catch (Exception e) {
                Assert.notNull(mappedStatement, "没找到方法【" + method + "】\nstatementId【" + statementId + "】对应的sql映射信息");
            }
            sqlCommandType = mappedStatement.getSqlCommandType();
        }
        switch (sqlCommandType) {
            case INSERT: {
                Object param = method.convertArgsToSqlCommandParam(args);
                result = rowCountResult(sqlSession.insert(statementId, param), statementId);
                break;
            }
            case UPDATE: {
                Object param = method.convertArgsToSqlCommandParam(args);
                result = rowCountResult(sqlSession.update(statementId, param), statementId);
                break;
            }
            case DELETE: {
                Object param = method.convertArgsToSqlCommandParam(args);
                result = rowCountResult(sqlSession.delete(statementId, param), statementId);
                break;
            }
            case SELECT:
                if (method.returnsVoid() && method.hasResultHandler()) {
                    executeWithResultHandler(sqlSession, args, mappedStatement);
                    result = null;
                } else if (method.returnsMany()) {
                    result = executeForMany(sqlSession, args, statementId);
                } else if (method.returnsMap()) {
                    result = executeForMap(sqlSession, args, statementId);
                } else if (method.returnsCursor()) {
                    result = executeForCursor(sqlSession, args, statementId);
                } else {
                    Object param = method.convertArgsToSqlCommandParam(args);
                    result = sqlSession.selectOne(statementId, param);
                }
                break;
            case FLUSH:
                result = sqlSession.flushStatements();
                break;
            default:
                throw new BindingException("Unknown execution method for: " + statementId);
        }
        if (result == null && method.getReturnType().isPrimitive() && !method.returnsVoid()) {
            throw new BindingException("Mapper method '" + statementId + " attempted to return null from a method with a primitive return type (" + method.getReturnType() + ").");
        }
        return result;
    }

    private Object rowCountResult(int rowCount, String statementId) {
        final Object result;
        if (method.returnsVoid()) {
            result = null;
        } else if (Integer.class.equals(method.getReturnType()) || Integer.TYPE.equals(method.getReturnType())) {
            result = rowCount;
        } else if (Long.class.equals(method.getReturnType()) || Long.TYPE.equals(method.getReturnType())) {
            result = (long) rowCount;
        } else if (Boolean.class.equals(method.getReturnType()) || Boolean.TYPE.equals(method.getReturnType())) {
            result = rowCount > 0;
        } else {
            throw new BindingException("Mapper method '" + statementId + "' has an unsupported return type: " + method.getReturnType());
        }
        return result;
    }

    private void executeWithResultHandler(SqlSession sqlSession, Object[] args, MappedStatement mappedStatement) {
        String stateMentId = mappedStatement.getId();
        if (!StatementType.CALLABLE.equals(mappedStatement.getStatementType()) && void.class.equals(mappedStatement.getResultMaps().get(0).getType())) {
            throw new BindingException("method " + stateMentId + " needs either a @ResultMap annotation, a @ResultType annotation," + " or a resultType attribute in XML so a ResultHandler can be used as a parameter.");
        }
        Object param = method.convertArgsToSqlCommandParam(args);
        if (method.hasRowBounds()) {
            RowBounds rowBounds = method.extractRowBounds(args);
            sqlSession.select(stateMentId, param, rowBounds, method.extractResultHandler(args));
        } else {
            sqlSession.select(stateMentId, param, method.extractResultHandler(args));
        }
    }

    private <E> Object executeForMany(SqlSession sqlSession, Object[] args, String statementId) {
        List<E> result;
        Object param = method.convertArgsToSqlCommandParam(args);
        if (method.hasRowBounds()) {
            RowBounds rowBounds = method.extractRowBounds(args);
            result = sqlSession.<E>selectList(statementId, param, rowBounds);
        } else {
            result = sqlSession.<E>selectList(statementId, param);
        }
        // issue #510 Collections & arrays support
        if (!method.getReturnType().isAssignableFrom(result.getClass())) {
            if (method.getReturnType().isArray()) {
                return convertToArray(result);
            } else {
                return convertToDeclaredCollection(sqlSession.getConfiguration(), result);
            }
        }
        return result;
    }

    private <T> Cursor<T> executeForCursor(SqlSession sqlSession, Object[] args, String statementId) {
        Cursor<T> result;
        Object param = method.convertArgsToSqlCommandParam(args);
        if (method.hasRowBounds()) {
            RowBounds rowBounds = method.extractRowBounds(args);
            result = sqlSession.<T>selectCursor(statementId, param, rowBounds);
        } else {
            result = sqlSession.<T>selectCursor(statementId, param);
        }
        return result;
    }

    private <E> Object convertToDeclaredCollection(Configuration config, List<E> list) {
        Object collection = config.getObjectFactory().create(method.getReturnType());
        MetaObject metaObject = config.newMetaObject(collection);
        metaObject.addAll(list);
        return collection;
    }

    @SuppressWarnings("unchecked")
    private <E> Object convertToArray(List<E> list) {
        Class<?> arrayComponentType = method.getReturnType().getComponentType();
        Object array = Array.newInstance(arrayComponentType, list.size());
        if (arrayComponentType.isPrimitive()) {
            for (int i = 0; i < list.size(); i++) {
                Array.set(array, i, list.get(i));
            }
            return array;
        } else {
            return list.toArray((E[]) array);
        }
    }

    private <K, V> Map<K, V> executeForMap(SqlSession sqlSession, Object[] args, String statementId) {
        Map<K, V> result;
        Object param = method.convertArgsToSqlCommandParam(args);
        if (method.hasRowBounds()) {
            RowBounds rowBounds = method.extractRowBounds(args);
            result = sqlSession.<K, V>selectMap(statementId, param, method.getMapKey(), rowBounds);
        } else {
            result = sqlSession.<K, V>selectMap(statementId, param, method.getMapKey());
        }
        return result;
    }
}
