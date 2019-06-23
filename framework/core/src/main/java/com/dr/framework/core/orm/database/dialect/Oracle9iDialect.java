package com.dr.framework.core.orm.database.dialect;

import java.sql.Timestamp;
import java.sql.Types;

/**
 * @author dr
 */
public class Oracle9iDialect extends Oracle8iDialect {

    public Oracle9iDialect() {
        //字符串
        registerClass(1, String.class)
                .registerType(Types.CHAR, 1, "char(1 char)")
                .registerType(Types.NCHAR, 1, "nchar(1)")
                .registerType(Types.VARCHAR, 4000, "varchar2($l char)")
                .registerType(Types.NVARCHAR, 4000, "nvarchar2($l)")
                .registerType(Types.LONGVARCHAR, 2147483647, "long")
                .registerType(Types.LONGNVARCHAR, 2147483647, "long")
                .registerType(Types.SQLXML, 2147483647, "long");

        registerClass(1, Timestamp.class).registerType(Types.TIMESTAMP, "timestamp");

    }

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
}
