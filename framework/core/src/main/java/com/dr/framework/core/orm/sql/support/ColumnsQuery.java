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
        return Stream.of(column.getTable(), column.getName(), column.getFunction())
                .collect(Collectors.joining("_"))
                .toUpperCase();
    }


    public void bindRelation(Relation<? extends Column> relation) {
        if (includeAll) {
            relation.getColumns()
                    .stream()
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
                        .entrySet()
                        .stream()
                        .map(e -> formatSql(e.getValue(), tableAlias))
                        .collect(Collectors.toList())
                , ""
                , ""
                , ", ");
        return builder.toString();
    }
}
