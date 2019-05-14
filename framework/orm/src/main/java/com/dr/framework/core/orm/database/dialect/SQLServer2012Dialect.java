package com.dr.framework.core.orm.database.dialect;

import com.dr.framework.core.orm.database.Dialect;

public class SQLServer2012Dialect extends Dialect {
    @Override
    public String parseToPageSql(String sqlSource, int offset, int limit) {
        StringBuilder builder = new StringBuilder(sqlSource);
        builder.append(" OFFSET ").append(offset).append(" ROWS FETCH NEXT ");
        builder.append(limit).append(" ROWS ONLY");
        return builder.toString();
    }

    @Override
    protected String getName() {
        return "sqlserver";
    }
}
