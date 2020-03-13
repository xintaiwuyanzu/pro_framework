package com.dr.framework.core.orm.database.dialect;

import com.dr.framework.core.orm.database.Dialect;
import com.dr.framework.core.orm.jdbc.Relation;

import java.sql.*;

/**
 * @author dr
 */
public class PostgreSQLDialect extends Dialect {
    public static final String NAME = "postgre";

    public PostgreSQLDialect() {
        //字符串
        registerClass(String.class)
                .registerType(Types.CHAR, 1, "char(1)")
                .registerType(Types.NCHAR, 1, "char(1)")
                .registerType(Types.VARCHAR, 255, "varchar($l)")
                .registerType(Types.NVARCHAR, 255, "varchar($l)")
                .registerType(Types.LONGVARCHAR, 16777215, "text")
                .registerType(Types.LONGNVARCHAR, 16777215, "text")
                .registerType(Types.SQLXML, 4294967295L, "text");
        //数字
        registerClass(int.class, Integer.class
                , double.class, Double.class
                , float.class, Float.class
                , long.class, Long.class)
                //2的8次方  -127到127
                .registerType(Types.SMALLINT, 4, "smallint")
                .registerType(Types.INTEGER, 10, "integer")
                .registerType(Types.BIGINT, 19, "bigint")
                .registerType(Types.DECIMAL, 65, "numeric($p,$s)")
                .registerType(Types.NUMERIC, 65, "numeric($p,$s)")
                .registerType(Types.REAL, 65, "numeric($p,$s)")
                .registerType(Types.DOUBLE, 65, "numeric($p,$s)")
                .registerType(Types.FLOAT, 65, "numeric($p,$s)");
        //日期
        registerClass(Date.class).registerType(Types.DATE, "date");
        registerClass(Time.class).registerType(Types.TIME, "time");
        registerClass(Timestamp.class).registerType(Types.TIMESTAMP, "timestamp");
        registerClass(java.util.Date.class).registerType(Types.TIMESTAMP, "timestamp");
        //blob
        registerClass(Blob.class, Byte[].class, byte[].class)
                .registerType(Types.BLOB, "oid")
                .registerType(Types.BINARY, "bytea")
                .registerType(Types.VARBINARY, 255, "bytea")
                .registerType(Types.LONGVARBINARY, 16777215, "bytea");
        //clob
        registerClass(Clob.class).registerType(Types.CLOB, "text");
        registerClass(NClob.class).registerType(Types.NCLOB, "text");
        //boolean
        registerClass(boolean.class, Boolean.class).registerType(Types.BOOLEAN, "bool");
        registerClass(boolean.class, Boolean.class).registerType(Types.BIT, "bool");
    }

    @Override
    public String parseToPageSql(String sqlSource, int offset, int limit) {
        StringBuilder builder = new StringBuilder(sqlSource);
        builder.append(" limit ").append(limit).append(" offset ").append(offset);
        return builder.toString();
    }

    @Override
    public String convertObjectName(String source) {
        return source.toLowerCase();
    }

    @Override
    protected String getName() {
        return NAME;
    }

    @Override
    protected String getDropPrimaryKeySql(Relation jdbcTable) {
        return String.format("%s drop constraint %s", getAlterTableString(jdbcTable.getName()), jdbcTable.getPrimaryKeyName());
    }
}
