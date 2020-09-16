package com.dr.framework.core.orm.database.dialect;

import com.dr.framework.core.orm.database.Dialect;
import com.dr.framework.core.orm.jdbc.Column;

import java.sql.*;

/**
 * @author dr
 */
public class H2Dialect extends Dialect {
    public static final String NAME = "h2";

    public H2Dialect() {
        //字符串
        registerClass(String.class)
                .registerType(Types.CHAR, 1, "char(1)")
                .registerType(Types.NCHAR, 1, "char(1)")
                .registerType(Types.VARCHAR, 255, "varchar($l)")
                .registerType(Types.NVARCHAR, 255, "varchar($l)")
                .registerType(Types.LONGVARCHAR, 65535, "varchar($l)")
                .registerType(Types.LONGNVARCHAR, 65535, "varchar($l)")
                .registerType(Types.SQLXML, 65535, "longtext");
        //数字
        registerClass(int.class, Integer.class
                , double.class, Double.class
                , float.class, Float.class
                , long.class, Long.class)
                //2的8次方  -127到127
                .registerType(Types.TINYINT, 2, "tinyint")
                .registerType(Types.SMALLINT, 4, "smallint")
                .registerType(Types.INTEGER, 8, "mediumint")
                .registerType(Types.INTEGER, 10, "integer")
                .registerType(Types.BIGINT, 19, "bigint")
                /**
                 * DECIMAL[(M[,D])]
                 * The maximum number of digits (M) for DECIMAL is 65.
                 * The maximum number of supported decimals (D) is 30.
                 * If D is omitted, the default is 0.
                 * If M is omitted, the default is 10.
                 */
                .registerType(Types.DECIMAL, 53, "decimal($p,$s)")
                .registerType(Types.NUMERIC, 53, "decimal($p,$s)")
                .registerType(Types.REAL, 53, "decimal($p,$s)")
                .registerType(Types.DOUBLE, 53, "decimal($p,$s)")
                .registerType(Types.FLOAT, 53, "decimal($p,$s)");
        //日期
        registerClass(Date.class).registerType(Types.DATE, "date");
        registerClass(Time.class).registerType(Types.TIME, "time");
        registerClass(Timestamp.class).registerType(Types.TIMESTAMP, "timestamp");
        registerClass(java.util.Date.class).registerType(Types.TIMESTAMP, "timestamp");
        //blob
        registerClass(Blob.class, Byte[].class, byte[].class)
                .registerType(Types.BLOB, "blob")
                .registerType(Types.BINARY, "binary($l)")
                .registerType(Types.VARBINARY, 65535, "binary($l)")
                .registerType(Types.LONGVARBINARY, 16777215, "longvarbinary");
        //clob
        registerClass(Clob.class).registerType(Types.CLOB, "clob");
        registerClass(NClob.class).registerType(Types.NCLOB, "clob");
        //boolean
        registerClass(boolean.class, Boolean.class).registerType(Types.BOOLEAN, "boolean");
        registerClass(boolean.class, Boolean.class).registerType(Types.BIT, "boolean");
    }

    @Override
    public String parseToPageSql(String sqlSource, int offset, int limit) {
        StringBuilder builder = new StringBuilder(sqlSource);
        builder.append(" limit ").append(limit);
        builder.append(" offset ").append(offset);
        return builder.toString();
    }

    @Override
    protected String getName() {
        return NAME;
    }

    @Override
    public String convertObjectName(String source) {
        return source.toUpperCase();
    }

    @Override
    protected String getRenameColumnSql(Column newColumn,Column oldColumn, String renameColumnName) {
        return String.format(" %s alter column %s rename to  %s "
                , getAlterTableString(newColumn.getTableName())
                , convertObjectName(newColumn.getName())
                , convertObjectName(renameColumnName));
    }
}
