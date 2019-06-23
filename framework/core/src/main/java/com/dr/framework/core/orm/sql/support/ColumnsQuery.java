package com.dr.framework.core.orm.sql.support;

import com.dr.framework.core.orm.sql.Column;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

class ColumnsQuery extends AbstractSqlQuery {
    private List<Column> columns = new ArrayList<>();

    void column(Column... columns) {
        if (columns != null) {
            this.columns.addAll(Arrays.asList(columns));
        }
    }

    void exclude(Column... columns) {
        if (columns != null) {
            for (Column column : columns) {
                this.columns.remove(column);
            }
        }
    }

    int columnSize() {
        return columns.size();
    }


    @Override
    String sql(TableAlias tableAlias, SqlQuery sqlQuery) {
        StringBuilder builder = new StringBuilder();
        List<String> sqlParts = columns.stream().map(column -> formatSql(column, tableAlias)).collect(Collectors.toList());
        sqlClause(builder, "", sqlParts, "", "", ", ");
        return builder.toString();
    }
}
