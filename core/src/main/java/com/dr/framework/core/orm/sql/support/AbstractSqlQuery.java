package com.dr.framework.core.orm.sql.support;

import com.dr.framework.common.service.DefaultDataBaseService;
import com.dr.framework.core.orm.database.Dialect;
import com.dr.framework.core.orm.sql.Column;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

abstract class AbstractSqlQuery {

    static final String AND_NEW = ")) \nAND ((";
    static final String OR_NEW = ")) \nOR ((";
    static final String AND = " ) AND (";
    static final String OR = " ) OR (";
    static final String AND_ = " AND ";

    static final List<String> concats = Arrays.asList(AND, AND_NEW, OR, OR_NEW);

    void sqlClause(StringBuilder builder, String keyword, List<String> parts, String open, String close, String conjunction) {
        if (parts != null && !parts.isEmpty()) {
            builder.append(keyword);
            builder.append(" ");
            builder.append(open);
            int partsSize = parts.size();
            String last = "________";
            for (int i = 0; i < partsSize; i++) {
                String part = parts.get(i);
                boolean ex = i > 0 && !(concats.contains(last) || concats.contains(part));
                if (ex) {
                    builder.append(conjunction);
                }
                builder.append(part);
                last = part;
            }
            builder.append(close);
        }
    }

    /**
     * 将对象转换成sql语句
     *
     * @param tableAlias 表别名对象
     * @param sqlQuery
     * @return
     */
    abstract String sql(TableAlias tableAlias, SqlQuery sqlQuery);

    static String formatSql(Column column, TableAlias tableAlias, SqlQuery sqlQuery) {
        return formatSql(column, tableAlias, true, sqlQuery);
    }

    static String formatSql(Column column, TableAlias tableAlias, boolean withAlias, SqlQuery sqlQuery) {
        StringBuilder sb = new StringBuilder();
        boolean hasFunction = StringUtils.hasText(column.getFunction());
        Dialect dialect = sqlQuery.getDialect();
        if (hasFunction) {
            String columnStr = "";
            if (StringUtils.hasText(column.getTable())) {
                columnStr += tableAlias.alias(column.getTable()) + ".";
            }
            columnStr += dialect == null ? column.getName() : dialect.convertColumnName(column.getName());
            String template = column.getFunction().indexOf('(') > -1 ? column.getFunction() : column.getFunction() + "(%s)";
            sb.append(String.format(template, columnStr));
        } else {
            if (StringUtils.hasText(column.getTable())) {
                sb.append(tableAlias.alias(column.getTable()));
                sb.append(".");
            }
            sb.append(dialect == null ? column.getName() : dialect.convertColumnName(column.getName()));
        }
        if (withAlias && StringUtils.hasText(column.getAlias())) {
            sb.append(" as ");
            String fix = DefaultDataBaseService.getInstance().getColumFix(sqlQuery.getRelation());
            //别名加转义符，防止数据库大小写敏感转义
            sb.append(fix + column.getAlias() + fix);
        }
        return sb.toString();
    }
}
