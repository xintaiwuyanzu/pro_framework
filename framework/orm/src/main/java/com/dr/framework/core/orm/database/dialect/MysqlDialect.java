package com.dr.framework.core.orm.database.dialect;

import com.dr.framework.core.orm.database.Dialect;

public class MysqlDialect extends Dialect {

    @Override
    public String parseToPageSql(String sqlSource, int offset, int limit) {
        StringBuilder builder = new StringBuilder(sqlSource);
        builder.append(" limit ").append(offset).append(",").append(limit);
        return builder.toString();
    }

    @Override
    protected String getName() {
        return "mysql";
    }
}
