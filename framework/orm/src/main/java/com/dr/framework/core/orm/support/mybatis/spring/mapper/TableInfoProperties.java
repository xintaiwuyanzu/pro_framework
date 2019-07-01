package com.dr.framework.core.orm.support.mybatis.spring.mapper;

import com.dr.framework.core.orm.module.EntityRelation;
import com.dr.framework.core.orm.sql.support.SqlQuery;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.JdbcType;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.sql.Types;
import java.util.Properties;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author dr
 */
class TableInfoProperties extends Properties {
    private static final String TABLE_KEY = "table";
    private static final String VALUES_KEY = "values";
    private static final String VALUES_TEST_KEY = "valuestest";
    private static final String PK_KEY = "pk";
    private static final String PK_KEY_ALIAS = "id";
    private static final String COLUMNS_KEY = "columns";
    private static final String SET_KEY = "set";
    private static final String SET_TEST_KEY = "settest";
    private static final String IN_KEY = "in";
    private static final String QUERY = "query";
    private EntityRelation tableInfo;
    private boolean isInsert;
    private String tableAlias = "A";
    private String queryTemplate = "${%s}";
    boolean pkUsed = false;
    private boolean hasSqlQuery = false;

    TableInfoProperties(EntityRelation tableInfo, Method method) {
        this.tableInfo = tableInfo;
        init(method);
    }

    private void init(Method method) {
        isInsert = method.isAnnotationPresent(Insert.class);

        boolean hasParamAnnotation = false;
        int paramCount = 0;
        String sqlQueryParamKey = null;
        Class<?>[] paramTypes = method.getParameterTypes();
        Annotation[][] paramAnnotations = method.getParameterAnnotations();
        for (int paramIndex = 0; paramIndex < paramAnnotations.length; paramIndex++) {
            Class type = paramTypes[paramIndex];
            if (RowBounds.class.isAssignableFrom(type) || ResultHandler.class.isAssignableFrom(type)) {
                continue;
            }
            paramCount++;
            String name = null;
            for (Annotation annotation : paramAnnotations[paramIndex]) {
                if (annotation instanceof Param) {
                    hasParamAnnotation = true;
                    name = ((Param) annotation).value();
                    break;
                }
            }
            if (name == null) {
                name = "param" + paramCount;
            }
            if (type == SqlQuery.class) {
                sqlQueryParamKey = name;
                hasSqlQuery = true;
            }
        }
        if (!StringUtils.isEmpty(sqlQueryParamKey)) {
            boolean can = (paramCount == 1 && hasParamAnnotation) || paramCount > 1;
            if (can) {
                tableAlias = String.format("${%s.$alias%s}", sqlQueryParamKey, tableInfo.getName());
                queryTemplate = "${" + sqlQueryParamKey + ".%s}";
            }
        }
    }


    @Override
    public synchronized boolean containsKey(Object key) {
        boolean contains = super.containsKey(key);
        if (contains) {
            return true;
        } else {
            String keyStr = key.toString().trim();
            return keyStr.equalsIgnoreCase(TABLE_KEY)
                    || keyStr.equalsIgnoreCase(VALUES_KEY)
                    || keyStr.equalsIgnoreCase(VALUES_TEST_KEY)
                    || keyStr.equalsIgnoreCase(PK_KEY)
                    || keyStr.equalsIgnoreCase(PK_KEY_ALIAS)
                    || keyStr.equalsIgnoreCase(SET_KEY)
                    || keyStr.equalsIgnoreCase(SET_TEST_KEY)
                    || keyStr.equalsIgnoreCase(COLUMNS_KEY)
                    || (keyStr.startsWith(IN_KEY) && keyStr.contains("#"))
                    || (keyStr.startsWith(QUERY) && keyStr.contains("#"));
        }
    }

    @Override
    public String getProperty(String key) {
        String value = super.getProperty(key);
        if (StringUtils.isEmpty(value)) {
            String[] arr = key.split("#");
            key = arr[0];
            String append = arr.length == 2 ? arr[1] : "";
            switch (key.trim().toLowerCase()) {
                case TABLE_KEY:
                    value = isInsert ? tableInfo.getName() : tableInfo.getName() + " " + tableAlias;
                    break;
                case VALUES_KEY:
                    value = String.format("(%s) values (%s)", join(false, "%s", ","), join(false, "#{%2$s,jdbcType=%4$s}", ","));
                    break;
                case VALUES_TEST_KEY:
                    value = String.format("<trim prefix='(' suffix=')' suffixOverrides=','>%s</trim><trim prefix='values (' suffix=')' suffixOverrides=','>%s</trim>",
                            join(false, column ->
                                            isDate(column.getType()) ?
                                                    "<if test=\"%2$s!=null\">%1$s,</if>"
                                                    : "<if test='%2$s!=null and %2$s !=\"\" or %2$s==0'>%1$s,</if>"
                                    , ""),
                            join(false, column ->
                                            isDate(column.getType()) ?
                                                    "<if test='%2$s!=null'>#{%2$s,jdbcType=%4$s},</if>"
                                                    : "<if test='%2$s!=null and %2$s !=\"\" or %2$s==0'>#{%2$s,jdbcType=%4$s},</if>"
                                    , ""));
                    break;
                case PK_KEY:
                    //TODO 处理没有主键和联合主键的情况
                    value = tableAlias + "." + tableInfo.primaryKeyColumns().stream().collect(Collectors.joining());
                    pkUsed = true;
                    break;
                case PK_KEY_ALIAS:
                    value = "#{" + tableInfo.getPrimaryKeyAlias() + "}";
                    break;
                case COLUMNS_KEY:
                    value = join(false, "%3$s.%1$s as %2$s", ",");
                    break;
                case SET_KEY:
                    value = " set " + join(true, "%3$s.%1$s = #{%2$s,jdbcType=%4$s}", ",");
                    break;
                case SET_TEST_KEY:
                    value = String.format(
                            hasSqlQuery ?
                                    "<set><if test=\"$set!=null\">${$setQ},</if>%s</set>"
                                    : "<set>%s</set>"
                            , join(true
                                    , column ->
                                            isDate(column.getType()) ?
                                                    "<if test=\"%2$s!=null\">%3$s.%1$s=#{%2$s,jdbcType=%4$s},</if>"
                                                    : "<if test=\"%2$s!=null and %2$s!='' or %2$s==0\">%3$s.%1$s=#{%2$s,jdbcType=%4$s},</if>"
                                    , ""));
                    break;
                case IN_KEY:
                    value = String.format("in <foreach item='item' index='index' collection='%s' open='(' separator=',' close=')'>#{item}</foreach>", append);
                    break;
                case QUERY:
                    value = String.format(queryTemplate, append);
                    break;
                default:
                    break;
            }
        }
        return value;
    }

    private boolean isDate(int type) {
        return type == Types.DATE
                || type == Types.TIME
                || type == Types.TIMESTAMP_WITH_TIMEZONE
                || type == Types.TIMESTAMP
                || type == Types.TIME_WITH_TIMEZONE;
    }


    /**
     * 主要是处理不同类型的数据字段，判读空表达式不同
     *
     * @param filterId
     * @param templateFunction
     * @param delimiter
     * @return
     */
    private String join(boolean filterId, Function<EntityRelation.FieldColumn, String> templateFunction, CharSequence delimiter) {
        return columnStream(filterId)
                .map(column ->
                        String.format(templateFunction.apply(column)
                                , column.getName()
                                , column.getAlias()
                                , tableAlias
                                , JdbcType.forCode(column.getType()).name()
                        )
                )
                .collect(Collectors.joining(delimiter));
    }

    private String join(boolean filterId, String template, CharSequence delimiter) {
        return columnStream(filterId)
                .map(column ->
                        String.format(template
                                , column.getName()
                                , column.getAlias()
                                , tableAlias
                                , JdbcType.forCode(column.getType()).name()
                        )
                )
                .collect(Collectors.joining(delimiter));
    }

    private Stream<EntityRelation.FieldColumn> columnStream(boolean filterId) {
        Stream<EntityRelation.FieldColumn> stream = tableInfo.getColumns().stream();
        if (filterId) {
            stream = stream.filter(column -> !tableInfo.primaryKeyColumns().contains(column.getName()));
        }
        return stream;
    }
}
