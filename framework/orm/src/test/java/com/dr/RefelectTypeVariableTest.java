package com.dr;

import com.dr.entity.TestEntity1;
import com.dr.framework.common.entity.BaseStatusEntity;
import com.dr.framework.core.orm.database.dialect.Mysql57Dialect;
import com.dr.framework.core.orm.database.tools.AnnotationTableReader;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.function.Function;

public class RefelectTypeVariableTest {
    class Test<A, S> extends BaseStatusEntity<S> {
        private A s;
    }

    class Test1<B, C, D> extends Test<B, C> {
        private D d;

    }

    class Test2 extends Test1<Integer, String, Boolean> {

    }

    class Test3 extends BaseStatusEntity<String> {

    }

    static void read(LinkedList link, Class c) {
        Type type = c.getGenericSuperclass();
        if (!type.equals(Object.class)) {
            link.addFirst(type);
            Class clazz = null;
            if (type instanceof Class) {
                clazz = (Class) type;
            } else if (type instanceof ParameterizedType) {
                Type raw = ((ParameterizedType) type).getRawType();
                if (raw instanceof Class) {
                    clazz = (Class) raw;
                }
            }
            if (clazz != null) {
                read(link, clazz);
                for (Field field : clazz.getDeclaredFields()) {
                    Type type1 = field.getGenericType();
                    if (type1 instanceof TypeVariable) {
                        Class type2 = getFieldType(link, (TypeVariable) type1);
                        System.out.println(type2);
                        System.out.println(type1);
                    }
                }
            }
        }
    }

    private static Class getFieldType(LinkedList<Type> link, TypeVariable type1) {
        //先查找到声明位置
        GenericDeclaration genericDeclaration = type1.getGenericDeclaration();
        int index = Arrays.asList(genericDeclaration.getTypeParameters()).indexOf(type1);
        ParameterizedType declareType =
                link.stream()
                        .filter(type -> type instanceof ParameterizedType)
                        .filter(type -> ((ParameterizedType) type).getRawType().equals(genericDeclaration))
                        .findFirst()
                        .map(type -> (ParameterizedType) type)
                        .get();
        Type fieldType = declareType.getActualTypeArguments()[index];
        if (fieldType instanceof Class) {
            return (Class) fieldType;
        } else if (fieldType instanceof TypeVariable) {
            return getFieldType(link, (TypeVariable) fieldType);
        } else {
            return null;
        }
    }

    public static void main1(String[] args) {
        AnnotationTableReader annotationTableReader = new AnnotationTableReader();
        annotationTableReader.readerTableInfo(TestEntity1.class, new Mysql57Dialect());
    }


    public static void main(String[] args) {
        Function fn = RefelectTypeVariableTest::main1;
        System.out.println(fn);
        System.out.println(fn.getClass());
    }

    private static Object main1(Object o) {
        return "";
    }


    private static void read(Class test1Class) {
        LinkedList linkedList = new LinkedList();
        linkedList.addFirst(test1Class);
        read(linkedList, test1Class);
        System.out.println(linkedList);
    }
}
