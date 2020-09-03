package com.dr.framework.core.orm.sql.support;

import com.dr.framework.core.orm.sql.Column;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class SetQuery extends AbstractSqlQuery {
    Map<Column, Column> columnColumnMap = new HashMap<>();

    void column(Column left, Column right) {
        columnColumnMap.put(left, right);
    }

    @Override
    String sql(TableAlias tableAlias, SqlQuery sqlQuery) {
        if (!columnColumnMap.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            List<String> sqlParts = columnColumnMap
                    .entrySet()
                    .stream()
                    .map(entry ->
                            formatSql(entry.getKey(), tableAlias, false) +
                                    "=" +
                                    formatSql(entry.getValue(), tableAlias, false)
                    )
                    .collect(Collectors.toList());
            sqlClause(builder, "", sqlParts, "", "", ", ");
            return builder.toString();
        }
        return null;
    }
}
