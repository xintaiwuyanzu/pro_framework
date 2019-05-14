package com.dr.framework.core.orm.database.dialect;

import com.dr.framework.core.orm.database.Dialect;

class PostgreDialect extends Dialect {

    @Override
    public String parseToPageSql(String sqlSource, int offset, int limit) {
        StringBuilder builder = new StringBuilder(sqlSource);
        builder.append(" limit ").append(limit).append(" offset ").append(offset);
        return builder.toString();
    }

    @Override
    protected String getName() {
        return "postgre";
    }
}
