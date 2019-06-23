package com.dr.framework.core.orm.sql.support;

import com.dr.framework.core.orm.sql.Column;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 连表查询数据
 */
class JoinColumns {
    Map<String, List<Column[]>> columnMap = new HashMap<>();

    JoinColumns add(Column left, Column right) {
        String table = right.getTable().toUpperCase();
        List<Column[]> columns = columnMap.get(table);
        if (columns == null) {
            columns = new ArrayList<>();
        } else {
            if (columns.stream().anyMatch(columns1 -> (columns1[0].getName() + columns1[1].getName()).equalsIgnoreCase(left.getName() + right.getName()))) {
                return this;
            }
        }
        columns.add(new Column[]{left, right});
        columnMap.put(table, columns);
        return this;
    }

    private String formatColumn(TableAlias tableAlias, Column column) {
        StringBuilder builder = new StringBuilder(tableAlias.alias(column.getTable()));
        builder.append(".");
        builder.append(column.getName());
        return builder.toString();
    }


    List<String> parts(TableAlias tableAlias) {
        return columnMap.keySet().stream().map(table -> {
            List<Column[]> columns = columnMap.get(table);
            StringBuilder builder = new StringBuilder(table.toUpperCase());
            builder.append(" ");
            builder.append(tableAlias.alias(table));
            builder.append(" on ");
            String joins = columns.stream().map(cl -> formatColumn(tableAlias, cl[0]) + "=" + formatColumn(tableAlias, cl[1])).collect(Collectors.joining(AbstractSqlQuery.AND));
            builder.append(joins);
            return builder.toString();
        }).collect(Collectors.toList());
    }
}
