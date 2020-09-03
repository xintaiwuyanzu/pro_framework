package com.dr.framework.core.orm.sql.support;

import com.dr.framework.core.orm.jdbc.Relation;
import com.dr.framework.core.orm.sql.Column;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class ColumnsQuery extends AbstractSqlQuery {
    private Map<String, Column> columnMap = new HashMap<>();
    private List<String> excludes = new ArrayList<>();
    private boolean includeAll;

    String columnToKey(Column column) {
        return String.join("_", column.getTable(), column.getName(), column.getFunction())
                .toUpperCase();
    }


    public void bindRelation(Relation<? extends Column> relation) {
        if (includeAll) {
            relation.getColumns()
                    .forEach(c -> columnMap.put(columnToKey(c), c));
        }
        excludes.forEach(columnMap::remove);
    }

    public void setIncludeAll(boolean includeAll) {
        this.includeAll = includeAll;
    }

    void column(Column... columns) {
        if (columns != null) {
            Stream.of(columns)
                    .forEach(c -> columnMap.put(columnToKey(c), c));
        }
    }

    void exclude(Column... columns) {
        if (columns != null) {
            Stream.of(columns)
                    .forEach(c -> excludes.add(columnToKey(c)));
        }
    }

    int columnSize() {
        return columnMap.size();
    }

    @Override
    String sql(TableAlias tableAlias, SqlQuery sqlQuery) {
        StringBuilder builder = new StringBuilder();
        sqlClause(builder
                , ""
                , columnMap
                        .values()
                        .stream()
                        .map(column -> formatSql(column, tableAlias))
                        .collect(Collectors.toList())
                , ""
                , ""
                , ", ");
        return builder.toString();
    }
}
