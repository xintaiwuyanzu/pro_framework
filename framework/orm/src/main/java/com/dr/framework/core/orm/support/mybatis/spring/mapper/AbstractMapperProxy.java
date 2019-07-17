package com.dr.framework.core.orm.support.mybatis.spring.mapper;

import com.dr.framework.core.orm.annotations.Table;
import com.dr.framework.core.orm.sql.support.SqlQuery;
import com.dr.framework.core.orm.support.mybatis.spring.MybatisConfigurationBean;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.lang.UsesJava7;
import org.apache.ibatis.reflection.ExceptionUtil;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.*;

/**
 * @author dr
 */
public abstract class AbstractMapperProxy implements InvocationHandler, Serializable {
    protected Class<?> mapperInterface;

    public AbstractMapperProxy(Class<?> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            if (Object.class.equals(method.getDeclaringClass())) {
                return method.invoke(this, args);
            } else if (isDefaultMethod(method)) {
                return invokeDefaultMethod(proxy, method, args);
            }
        } catch (Throwable t) {
            throw ExceptionUtil.unwrapThrowable(t);
        }
        Class entityClass = findEntityClass(method, args);
        MybatisConfigurationBean mybatisConfigurationBean = findConfigBean(method, entityClass);
        Assert.notNull(mybatisConfigurationBean, "没有找到指定的config类，请检查配置是否正确：" + mapperInterface.getName());
        MapperMethod mapperMethod = cachedMapperMethod(method, entityClass, mybatisConfigurationBean);
        return mapperMethod.execute(mybatisConfigurationBean.getSqlSessionTemplate(), args);
    }

    protected abstract MapperMethod cachedMapperMethod(Method method, Class entityClass, MybatisConfigurationBean mybatisConfigurationBean);

    @UsesJava7
    protected Object invokeDefaultMethod(Object proxy, Method method, Object[] args)
            throws Throwable {
        final Constructor<MethodHandles.Lookup> constructor = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class, int.class);
        if (!constructor.isAccessible()) {
            constructor.setAccessible(true);
        }
        final Class<?> declaringClass = method.getDeclaringClass();
        return constructor.newInstance(declaringClass, MethodHandles.Lookup.PRIVATE | MethodHandles.Lookup.PROTECTED | MethodHandles.Lookup.PACKAGE | MethodHandles.Lookup.PUBLIC).unreflectSpecial(method, declaringClass)
                .bindTo(proxy).invokeWithArguments(args);
    }

    public static boolean isDefaultMethod(Method method) {
        return (method.getModifiers() & (Modifier.ABSTRACT | Modifier.PUBLIC | Modifier.STATIC)) == Modifier.PUBLIC && method.getDeclaringClass().isInterface();
    }

    /**
     * 根据实体类class查找对应的mybatis对象
     *
     * @param method
     * @param entityClass
     * @return
     */
    protected abstract MybatisConfigurationBean findConfigBean(Method method, Class entityClass);

    protected Class<?> findEntityClass(Method method, Object[] args) {
        if (args != null) {
            for (Object obj : args) {
                if (obj instanceof SqlQuery) {
                    return ((SqlQuery) obj).getEntityClass();
                }
                Class<?> clazz;
                if (obj instanceof Class) {
                    clazz = (Class<?>) obj;
                } else {
                    clazz = obj.getClass();
                }
                if (AnnotationUtils.isAnnotationDeclaredLocally(Table.class, clazz)) {
                    return clazz;
                }
            }
            Type returnType = method.getGenericReturnType();
            if (returnType instanceof Class) {
                if (((Class) returnType).isAnnotationPresent(Table.class)) {
                    return (Class<?>) returnType;
                }
            } else if (returnType instanceof GenericArrayType) {
                //TODO
            } else if (returnType instanceof WildcardType) {
                //TODO
            }
        }
        return null;
    }

}
