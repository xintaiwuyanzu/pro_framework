package com.dr.framework.core.orm.database.dialect;

import com.dr.framework.core.orm.database.Dialect;

public class OracleDialect extends Dialect {

    @Override
    public String parseToPageSql(String sqlSource, int offset, int limit) {
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT * FROM ( SELECT TMP.*, ROWNUM ROW_ID FROM ( ")
                .append(sqlSource)
                .append(" ) TMP WHERE ROWNUM <=")
                .append((offset >= 1) ? (offset + limit) : limit)
                .append(") WHERE ROW_ID > ")
                .append(offset);
        return builder.toString();
    }

    @Override
    protected String getName() {
        return "oracle";
    }
}
