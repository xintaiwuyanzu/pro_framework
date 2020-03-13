package com.dr.framework.core.orm.database;

import org.springframework.util.StringUtils;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author dr
 */
public final class ClassTypeHolder {
    List<Class> javaClasses = Collections.synchronizedList(new ArrayList<>());
    List<SqlType> sqlTypes = Collections.synchronizedList(new ArrayList<>());
    int order;

    public ClassTypeHolder(int order) {
        this.order = order;
    }

    public ClassTypeHolder registerClasses(Class... classes) {
        javaClasses.addAll(Arrays.asList(classes));
        return this;
    }

    public ClassTypeHolder registerType(int type, long scale, long precision, String nameHolder) {
        sqlTypes.add(new SqlType(type, scale, precision, nameHolder));
        return this;
    }

    public ClassTypeHolder registerType(int type, long length, String nameHolder) {
        sqlTypes.add(new SqlType(type, 0, length, nameHolder));
        return this;
    }

    public ClassTypeHolder registerType(int type, String nameHolder) {
        sqlTypes.add(new SqlType(type, 0, 0, nameHolder));
        return this;
    }

    int getType(Class clazz, int scale, int precision) {
        if (javaClasses.contains(clazz)) {
            if (sqlTypes.size() == 1) {
                return sqlTypes.get(0).type;
            }
            boolean classFloat = clazz.equals(float.class) || clazz.equals(Float.class) || clazz.equals(double.class) || clazz.equals(Double.class);
            for (SqlType sqlType : sqlTypes) {
                //浮点类型
                if (classFloat) {
                    if (Types.FLOAT == sqlType.type ||
                            Types.DECIMAL == sqlType.type ||
                            Types.DOUBLE == sqlType.type ||
                            Types.NUMERIC == sqlType.type ||
                            Types.REAL == sqlType.type
                    ) {
                        if (sqlType.match(scale, precision)) {
                            return sqlType.type;
                        }
                    }
                } else {
                    if (sqlType.match(scale, precision)) {
                        return sqlType.type;
                    }
                }
            }
        }
        return 0;
    }

    String getTypeName(int type, int scale, int precision) {
        for (SqlType sqlType : sqlTypes) {
            if (sqlType.type == type) {
                String name = sqlType.nameHolder;
                if (Dialect.isNumeric(type)) {
                    if (sqlType.match(scale, precision)) {
                        name = StringUtils.replace(name, "$p", String.valueOf(precision));
                        if (scale < 0) {
                            scale = 0;
                        }
                        name = StringUtils.replace(name, "$s", String.valueOf(scale));
                    } else {
                        continue;
                    }
                } else {
                    name = StringUtils.replace(name, "$l", String.valueOf(precision));
                }
                return name;
            }
        }
        return null;
    }

    public int getOrder() {
        return order;
    }

    static class SqlType {
        int type;
        //标度：小数点后几位
        long scale;
        //精度，字段长度
        long precision;
        String nameHolder;

        SqlType(int type, long scale, long precision, String nameHolder) {
            this.type = type;
            this.scale = scale;
            this.precision = precision;
            this.nameHolder = nameHolder;
        }

        boolean match(int scale, int precision) {
            return this.precision >= precision;
        }

    }
}
