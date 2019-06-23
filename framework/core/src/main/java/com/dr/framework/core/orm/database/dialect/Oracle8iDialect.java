package com.dr.framework.core.orm.database.dialect;

import com.dr.framework.core.orm.database.Dialect;
import com.dr.framework.core.orm.jdbc.Relation;
import org.springframework.util.StringUtils;

import java.sql.*;
import java.util.Locale;

/**
 * @author dr
 */
public class Oracle8iDialect extends Dialect {
    public static final String NAME = "oracle";

    public Oracle8iDialect() {
        //字符串
        registerClass(String.class)
                .registerType(Types.CHAR, 1, "char(1)")
                .registerType(Types.NCHAR, 1, "nchar(1)")
                .registerType(Types.VARCHAR, 4000, "varchar2($l)")
                .registerType(Types.NVARCHAR, 4000, "nvarchar2($l)")
                .registerType(Types.LONGVARCHAR, 2147483647, "long")
                .registerType(Types.LONGNVARCHAR, 2147483647, "long")
                .registerType(Types.SQLXML, 2147483647, "long");
        //数字
        registerClass(int.class, Integer.class
                , double.class, Double.class
                , float.class, Float.class
                , long.class, Long.class)
                //2的8次方  -127到127
                .registerType(Types.TINYINT, 3, "number(3,0)")
                .registerType(Types.SMALLINT, 5, "number(5,0)")
                .registerType(Types.INTEGER, 10, "number(10,0)")
                .registerType(Types.BIGINT, 19, "number(19,0)")

                .registerType(Types.DECIMAL, 38, "number($p,$s)")
                .registerType(Types.NUMERIC, 38, "number($p,$s)")
                .registerType(Types.REAL, 63, "float(63)")
                .registerType(Types.DOUBLE, 126, "float(126)")
                //TODO 随便写的
                .registerType(Types.FLOAT, 200, "float($p)");
        //日期
        registerClass(Date.class).registerType(Types.DATE, "date");
        registerClass(Time.class).registerType(Types.TIME, "date");
        registerClass(Timestamp.class).registerType(Types.TIMESTAMP, "date");
        registerClass(java.util.Date.class).registerType(Types.TIMESTAMP, "date");
        //blob
        registerClass(Blob.class, Byte[].class, byte[].class)
                .registerType(Types.BLOB, "blob")
                .registerType(Types.BINARY, "long raw")
                .registerType(Types.VARBINARY, "long raw")
                .registerType(Types.LONGVARBINARY, "long raw");
        //clob
        registerClass(Clob.class).registerType(Types.CLOB, "clob");
        registerClass(NClob.class).registerType(Types.NCLOB, "nclob");
        //boolean
        registerClass(boolean.class, Boolean.class).registerType(Types.BOOLEAN, "number(1,0)");
        registerClass(boolean.class, Boolean.class).registerType(Types.BIT, "number(1,0)");
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

    @Override
    public String convertObjectName(String source) {
        return StringUtils.hasLength(source) ? source.toUpperCase(Locale.ROOT).trim() : source;
    }

    @Override
    public boolean supportCommentOn() {
        return true;
    }

    @Override
    protected String getAddColumnString() {
        return "add";
    }

    @Override
    protected String getName() {
        return NAME;
    }

    @Override
    protected String getModifyColumnString() {
        return "modify";
    }

    @Override
    protected String getDropPrimaryKeySql(Relation jdbcTable) {
        return String.format("%s drop primary key drop index", getAlterTableString(jdbcTable.getName()));
    }
}
