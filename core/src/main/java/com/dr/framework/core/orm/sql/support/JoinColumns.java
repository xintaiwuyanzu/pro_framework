package com.dr.framework.core.orm.sql.support;

import com.dr.framework.core.orm.sql.Column;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 连表查询数据
 */
class JoinColumns {
    static class JoinOrder {
        JoinOrder(String table, int order) {
            this.table = table;
            this.order = order;
        }

        String table;
        int order;
    }

    List<JoinOrder> joinOrders = new ArrayList<>();
    Map<String, List<Column[]>> columnMap = new HashMap<>();

    JoinColumns add(Column left, Column right) {
        return add(left, right, 0);
    }

    JoinColumns add(Column left, Column right, int order) {
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
        joinOrders.add(new JoinOrder(table, order));
        return this;
    }

    private String formatColumn(TableAlias tableAlias, Column column) {
        StringBuilder builder = new StringBuilder(tableAlias.alias(column.getTable()));
        builder.append(".");
        builder.append(column.getName());
        return builder.toString();
    }


    List<String> parts(TableAlias tableAlias) {
        joinOrders.sort(Comparator.comparingInt(j -> j.order));
        return joinOrders.stream()
                .map(j -> {
                    String table = j.table;
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
